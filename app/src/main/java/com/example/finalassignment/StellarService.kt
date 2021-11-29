package com.example.finalassignment

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.stellar.sdk.*
import org.stellar.sdk.responses.AccountResponse
import org.stellar.sdk.responses.SubmitTransactionResponse
import org.stellar.sdk.responses.operations.OperationResponse
import java.io.InputStream
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


object StellarService {
    private val server = Server("https://horizon-testnet.stellar.org")

    suspend fun createNewAccount(): KeyPair {
        val keyPair: KeyPair? = KeyPair.random()

        val friendbotUrl = String.format(
            "https://friendbot.stellar.org/?addr=%s",
            keyPair!!.accountId
        )

        val response: InputStream = URL(friendbotUrl).openStream()
        val body: String = Scanner(response, "UTF-8").useDelimiter("\\A").next()

        return keyPair
    }

    suspend fun getTransactionsByPublicKey(publicKey: String): ArrayList<OperationResponse> {
        val keyPair: KeyPair =
            KeyPair.fromAccountId(publicKey)

        val response = server.payments().forAccount(keyPair.accountId).execute()
        return response.records
    }

    suspend fun getBalanceByPublicKey(publicKey: String): Double {
        val keyPair: KeyPair =
            KeyPair.fromAccountId(publicKey)

        val account: AccountResponse = server.accounts().account(keyPair.getAccountId())
        for (balance in account.balances) {
            if (balance.assetType == "native"){
                return balance.balance.toDouble()
            }
        }
        return 0.00
    }

    suspend fun checkAccountExists(publicKey: String): Boolean {
        var exists = true
        try {
            val destination: KeyPair = KeyPair.fromAccountId(publicKey)
            server.accounts().account(destination.getAccountId())
        } catch (e: Exception) {
            exists = false
        }
        return exists
    }


    suspend fun sendTransaction(source: String, destination: String, amount: Double): Boolean {
        var successful: Boolean = true

        val source: KeyPair =
            KeyPair.fromSecretSeed(source)
        val destination: KeyPair =
            KeyPair.fromAccountId(destination)


        server.accounts().account(destination.getAccountId())

        val sourceAccount: AccountResponse = server.accounts().account(source.getAccountId())

        val transaction = Transaction.Builder(sourceAccount, Network.TESTNET)
            .addOperation(
                PaymentOperation.Builder(
                    destination.getAccountId(),
                    AssetTypeNative(),
                    amount.toString()
                ).build()
            )
            .setTimeout(180)
            .setBaseFee(Transaction.MIN_BASE_FEE)
            .build()

        transaction.sign(source)

        try {
            val response: SubmitTransactionResponse = server.submitTransaction(transaction)
            println("Success!")
            println(response)
            val account: AccountResponse = server.accounts().account(destination.getAccountId())
            System.out.println("Balances for account " + destination.getAccountId())
            for (balance in account.balances) {
                System.out.printf(
                    "Type: %s, Code: %s, Balance: %s%n",
                    balance.assetType,
                    balance.assetCode,
                    balance.balance
                )

            }
            val payments = server.payments().forAccount(destination.getAccountId()).execute()
            println(payments)
        } catch (e: Exception) {
            successful = false
        }
        return successful
    }
}