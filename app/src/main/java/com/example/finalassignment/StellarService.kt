package com.example.finalassignment

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.stellar.sdk.*
import org.stellar.sdk.responses.AccountResponse
import org.stellar.sdk.responses.SubmitTransactionResponse
import org.stellar.sdk.responses.operations.OperationResponse
import org.stellar.sdk.responses.operations.PaymentOperationResponse
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

    suspend fun getTransactionsByPublicKey(publicKey: String): ArrayList<PaymentOperationResponse> {
        val payments = arrayListOf<PaymentOperationResponse>()

        val waitFor = CoroutineScope(Dispatchers.IO).async {
            val keyPair: KeyPair =
                KeyPair.fromAccountId(publicKey)
        try {
            var response = server.payments().forAccount(keyPair.accountId).limit(200).execute()
            while (response.records.isNotEmpty()) {
                var pagingToken = ""
                for (record in response.records) {
                    if (record is PaymentOperationResponse) {
                        payments.add(record)
                        pagingToken = record.pagingToken
                    }
                }
                response = server.payments().forAccount(keyPair.accountId).cursor(pagingToken).limit(10).execute()
            }
        } catch (e: Exception) {

        }
            return@async payments
        }
        waitFor.await()
        return payments
    }

    suspend fun getBalanceByPublicKey(publicKey: String): Double {
        var accountBalance: Double = 0.00

        val waitFor = CoroutineScope(Dispatchers.IO).async {
            try {
                val keyPair: KeyPair =
                    KeyPair.fromAccountId(publicKey)

                val account: AccountResponse = server.accounts().account(keyPair.getAccountId())
                for (balance in account.balances) {
                    if (balance.assetType == "native"){
                        accountBalance = balance.balance.toDouble()
                        return@async accountBalance
                    }
                }
            }
            catch (e: Exception) {
            }
        }
        waitFor.await()
        return accountBalance
    }

    suspend fun checkAccountExists(publicKey: String): Boolean {
        var exists = true

        val waitFor = CoroutineScope(Dispatchers.IO).async {
            try {
                val destination: KeyPair = KeyPair.fromAccountId(publicKey)
                server.accounts().account(destination.accountId)
            } catch (e: Exception) {
                exists = false
            }
            return@async exists
        }
        waitFor.await()
        return exists
    }


    suspend fun sendTransaction(source: String, destination: String, amount: Double): Boolean {
        var successful: Boolean = false

        val waitFor = CoroutineScope(Dispatchers.IO).async {
            val source: KeyPair =
                KeyPair.fromSecretSeed(source)
            val destination: KeyPair =
                KeyPair.fromAccountId(destination)

            try {
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


                val response: SubmitTransactionResponse = server.submitTransaction(transaction)
                if (response.isSuccess) {
                    successful = true
                }
            } catch (e: Exception) {
                successful = false
            }
            return@async successful
        }
        waitFor.await()
        return successful
    }
}