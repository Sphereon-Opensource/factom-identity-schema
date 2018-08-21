/*
 * Copyright 2018 Blockchain Innovation Foundation <https://blockchain-innovation.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.blockchain_innovation.factom.client;

import org.blockchain_innovation.factom.client.data.FactomException;
import org.blockchain_innovation.factom.client.data.model.Address;
import org.blockchain_innovation.factom.client.data.model.Chain;
import org.blockchain_innovation.factom.client.data.model.Entry;
import org.blockchain_innovation.factom.client.data.model.Range;
import org.blockchain_innovation.factom.client.data.model.response.walletd.AddressResponse;
import org.blockchain_innovation.factom.client.data.model.response.walletd.AddressesResponse;
import org.blockchain_innovation.factom.client.data.model.response.walletd.BlockHeightTransactionResponse;
import org.blockchain_innovation.factom.client.data.model.response.walletd.BlockHeightTransactionsResponse;
import org.blockchain_innovation.factom.client.data.model.response.walletd.ComposeResponse;
import org.blockchain_innovation.factom.client.data.model.response.walletd.ComposeTransactionResponse;
import org.blockchain_innovation.factom.client.data.model.response.walletd.DeleteTransactionResponse;
import org.blockchain_innovation.factom.client.data.model.response.walletd.ExecutedTransactionResponse;
import org.blockchain_innovation.factom.client.data.model.response.walletd.GetHeightResponse;
import org.blockchain_innovation.factom.client.data.model.response.walletd.PropertiesResponse;
import org.blockchain_innovation.factom.client.data.model.response.walletd.TransactionResponse;
import org.blockchain_innovation.factom.client.data.model.response.walletd.TransactionsResponse;
import org.blockchain_innovation.factom.client.data.model.response.walletd.WalletBackupResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WalletdClientTest extends AbstractClientTest {

    private static String transactionName = "TransactionName";
    private static String entryCreditAddress /*= "EC3cqLZPq5ypwRB5CLfXnud5vkWAV2sd235CFf9KcWcE3FH9GRxv"*/;
    private static String transactionId = "7552f169062885624ffbfb759c26c586121f43f5a49ee537ffa5ffb8f860eb10";
    private static int height = 40879;
    private static FactomResponse<ExecutedTransactionResponse> addFeeResponse;
    private final WalletdClient client = new WalletdClient();

    @Before
    public void setup() throws MalformedURLException {
        client.setUrl(new URL("http://136.144.204.97:8089/v2"));
    }

    @Test
    public void _00_properties() throws FactomException.ClientException {
        FactomResponse<PropertiesResponse> response = client.properties();
        assertValidResponse(response);

        PropertiesResponse properties = response.getResult();
        Assert.assertNotNull(properties);
        Assert.assertNotNull(properties.getWalletVersion());
        Assert.assertNotNull(properties.getWalletApiVersion());
    }

    @Test
    public void _01_newTransaction() throws FactomException.ClientException {
        transactionName = transactionName + System.currentTimeMillis();
        FactomResponse<TransactionResponse> response = client.newTransaction(transactionName);
        assertValidResponse(response);
        TransactionResponse transaction = response.getResult();

        Assert.assertNotNull(transaction);
        transactionId = transaction.getTxId();
        Assert.assertNotNull(transactionId);
        Assert.assertEquals(transactionName, transaction.getName());
    }

    @Test
    public void _02_generateEntryCreditAddress() throws FactomException.ClientException {
        FactomResponse<AddressResponse> response = client.generateEntryCreditAddress();
        assertValidResponse(response);

        AddressResponse address = response.getResult();
        Assert.assertNotNull(address);
        entryCreditAddress = address.getPublicAddress();
        Assert.assertNotNull(entryCreditAddress);
        Assert.assertNotNull(address.getSecret());
    }

    @Test
    public void _02_generateFactoidAddress() throws FactomException.ClientException {
        FactomResponse<AddressResponse> response = client.generateFactoidAddress();
        assertValidResponse(response);

        AddressResponse address = response.getResult();
        Assert.assertNotNull(address);
        Assert.assertNotNull(address.getPublicAddress());
        Assert.assertNotNull(address.getSecret());
    }

    @Test
    public void _11_importAddresses() throws FactomException.ClientException {
        String secret = "Fs1jQGc9GJjyWNroLPq7x6LbYQHveyjWNPXSqAvCEKpETNoTU5dP";

        Address address = new Address();
        address.setSecret(secret);
        List<Address> addresses = Collections.singletonList(address);

        FactomResponse<AddressesResponse> response = client.importAddresses(addresses);
        assertValidResponse(response);

        AddressesResponse addressesResponse = response.getResult();
        Assert.assertNotNull(addressesResponse);
        Assert.assertNotNull(addressesResponse.getAddresses());
        Assert.assertNotNull(addressesResponse.getAddresses().get(0));
        Assert.assertNotNull(addressesResponse.getAddresses().get(0).getPublicAddress());
        Assert.assertNotNull(secret, addressesResponse.getAddresses().get(0).getSecret());
    }

    @Test
    public void _11_importKoinify() throws FactomException.ClientException {
        String words = "yellow yellow yellow yellow yellow yellow yellow yellow yellow yellow yellow yellow";
        FactomResponse<AddressResponse> response = client.importKoinify(words);
        assertValidResponse(response);
    }

    @Test
    public void _12_address() throws FactomException.ClientException {
        FactomResponse<AddressResponse> response = client.address(entryCreditAddress);
        assertValidResponse(response);
        AddressResponse addressResponse = response.getResult();
        Assert.assertEquals(entryCreditAddress, addressResponse.getPublicAddress());
        Assert.assertNotNull(addressResponse.getSecret());
    }

    @Test
    public void _13_allAddresses() throws FactomException.ClientException {
        FactomResponse<AddressesResponse> response = client.allAddresses();
        assertValidResponse(response);

        AddressesResponse addresses = response.getResult();
        Assert.assertNotNull(addresses);
        Assert.assertNotNull(addresses.getAddresses());
        Assert.assertFalse(addresses.getAddresses().isEmpty());

        AddressResponse address = addresses.getAddresses().get(0);
        Assert.assertNotNull(address);
        Assert.assertNotNull(address.getSecret());
        Assert.assertNotNull(address.getPublicAddress());
    }

    @Test
    public void _14_tmpTransactions() throws FactomException.ClientException {
        FactomResponse<TransactionsResponse> response = client.tmpTransactions();
        assertValidResponse(response);

        TransactionsResponse transactions = response.getResult();
        Assert.assertNotNull(transactions);
        Assert.assertFalse(transactions.getTransactions().isEmpty());

        boolean found = false;
        for (TransactionResponse transaction : transactions.getTransactions()) {
            if (transactionName.equalsIgnoreCase(transaction.getName())) {
                Assert.assertEquals(transactionId, transaction.getTxId());
                Assert.assertEquals(transactionName, transaction.getName());
            }
        }
    }

    @Test
    public void _15_transactionsByAddress() throws FactomException.ClientException {
        FactomResponse<BlockHeightTransactionsResponse> response = client.transactionsByAddress(FACTOID_PUBLIC_KEY);
        assertValidResponse(response);

        BlockHeightTransactionsResponse transactions = response.getResult();
        Assert.assertNotNull(transactions);
        Assert.assertNotNull(transactions.getTransactions());
        Assert.assertFalse(transactions.getTransactions().isEmpty());
        transactionId = transactions.getTransactions().get(0).getTxId();
    }

    @Test
    public void _16_transactionsByAddress() throws FactomException.ClientException {
        FactomResponse<TransactionsResponse> response = client.transactionsByTransactionId(transactionId);
        assertValidResponse(response);

        TransactionsResponse transactions = response.getResult();
        Assert.assertNotNull(transactions);
        Assert.assertNotNull(transactions.getTransactions());
        Assert.assertFalse(transactions.getTransactions().isEmpty());
        Assert.assertEquals(transactionId, transactions.getTransactions().get(0).getTxId());
    }

    @Test
    public void _21_addEntryCreditOutput() throws FactomException.ClientException {
        long amount = 8;
        FactomResponse<TransactionResponse> response = client.addEntryCreditOutput(transactionName, entryCreditAddress, amount);
        assertValidResponse(response);

        TransactionResponse transaction = response.getResult();
        Assert.assertNotNull(transaction);
        Assert.assertEquals(8, transaction.getTotalEntryCreditOutputs());
        Assert.assertEquals(8, transaction.getEntryCreditOutputs().get(0).getAmount());
        Assert.assertEquals(entryCreditAddress, transaction.getEntryCreditOutputs().get(0).getAddress());
        transactionId = transaction.getTxId();
    }

    @Test
    public void _22_addInput() throws FactomException.ClientException {
        int amount = 10;
        FactomResponse<ExecutedTransactionResponse> response = client.addInput(transactionName, FACTOID_PUBLIC_KEY, amount);
        assertValidResponse(response);

        TransactionResponse transaction = response.getResult();
        Assert.assertNotNull(transaction);
        Assert.assertEquals(transactionName, transaction.getName());
        Assert.assertEquals(10, transaction.getTotalInputs());
        Assert.assertFalse(transaction.getInputs().isEmpty());
        Assert.assertEquals(10, transaction.getInputs().get(0).getAmount());
        Assert.assertEquals(FACTOID_PUBLIC_KEY, transaction.getInputs().get(0).getAddress());
    }

    @Test
    public void _22_addOutput() throws FactomException.ClientException {
        int amount = 2;
        FactomResponse<ExecutedTransactionResponse> response = client.addOutput(transactionName, FACTOID_PUBLIC_KEY, amount);
        assertValidResponse(response);

        TransactionResponse transaction = response.getResult();
        Assert.assertNotNull(transaction);
        Assert.assertEquals(transactionName, transaction.getName());
        Assert.assertEquals(2, transaction.getTotalOutputs());
        Assert.assertFalse(transaction.getInputs().isEmpty());
        Assert.assertEquals(2, transaction.getOutputs().get(0).getAmount());
        Assert.assertEquals(FACTOID_PUBLIC_KEY, transaction.getOutputs().get(0).getAddress());
    }

    @Test
    public void _23_addFee() throws FactomException.ClientException {
        addFeeResponse = client.addFee(transactionName, FACTOID_PUBLIC_KEY);
        assertValidResponse(addFeeResponse);

        TransactionResponse transaction = addFeeResponse.getResult();
        Assert.assertNotNull(transaction);
    }

    // @Test
    public void _24_subFee() throws FactomException.ClientException {
        // make output + ec equal to input after the fee is subtracted
        long amount = addFeeResponse.getResult().getTotalInputs() + addFeeResponse.getResult().getFeesRequired() - addFeeResponse.getResult().getTotalEntryCreditOutputs();
        FactomResponse<ExecutedTransactionResponse> addOutputResponse = client.addOutput(transactionName, FACTOID_PUBLIC_KEY, amount);
        assertValidResponse(addOutputResponse);

        FactomResponse<ExecutedTransactionResponse> response = client.subFee(transactionName, FACTOID_PUBLIC_KEY);
        assertValidResponse(response);

        TransactionResponse transaction = response.getResult();
        Assert.assertNotNull(transaction);
    }

    @Test
    public void _26_composeTransaction() throws FactomException.ClientException {
        FactomResponse<ComposeTransactionResponse> response = client.composeTransaction(transactionName);
        assertValidResponse(response);

        ComposeTransactionResponse composeTransaction = response.getResult();
        Assert.assertNotNull(composeTransaction);
        Assert.assertNotNull(composeTransaction.getParams());
        Assert.assertNotNull(composeTransaction.getParams().getTransaction());
    }

    @Test
    public void _27_signTransaction() throws FactomException.ClientException {
        FactomResponse<ExecutedTransactionResponse> response = client.signTransaction(transactionName);
        assertValidResponse(response);

        ExecutedTransactionResponse executedTransaction = response.getResult();
        Assert.assertNotNull(executedTransaction);
        Assert.assertTrue(executedTransaction.isSigned());
        transactionId = executedTransaction.getTxId();
    }

    @Test
    public void _29_deleteTransaction() throws FactomException.ClientException {
        FactomResponse<DeleteTransactionResponse> response = client.deleteTransaction(transactionName);
        assertValidResponse(response);

        DeleteTransactionResponse deletedTransaction = response.getResult();
        Assert.assertNotNull(deletedTransaction);
        Assert.assertEquals(transactionName, deletedTransaction.getName());
    }

    @Test
    public void _31_composeChain() throws FactomException.ClientException {
        List<String> externalIds = Arrays.asList(
                "61626364",
                "31323334");
        Chain.Entry firstEntry = new Chain.Entry();
        firstEntry.setExternalIds(externalIds);
        firstEntry.setContent("3132333461626364");
        Chain chain = new Chain();
        chain.setFirstEntry(firstEntry);

        FactomResponse<ComposeResponse> response = client.composeChain(chain, EC_PUBLIC_KEY);
        assertValidResponse(response);

        ComposeResponse composeResponse = response.getResult();
        Assert.assertNotNull(composeResponse.getCommit());
        Assert.assertNotNull(composeResponse.getCommit().getId());
        Assert.assertNotNull(composeResponse.getCommit().getParams());
        Assert.assertNotNull(composeResponse.getCommit().getParams().getMessage());
        Assert.assertNotNull(composeResponse.getReveal());
        Assert.assertNotNull(composeResponse.getReveal().getId());
        Assert.assertNotNull(composeResponse.getReveal().getParams());
        Assert.assertNotNull(composeResponse.getReveal().getParams().getEntry());
    }

    @Test
    public void _32_composeEntry() throws FactomException.ClientException {
        List<String> externalIds = Arrays.asList("cd90", "90cd");
        Entry entry = new Entry();
        entry.setChainId("041ffaf76eb6370c94701c7aa60cc8c114fc68ede00d28389bc31850ef732c4f");
        entry.setContent("abcdef");
        entry.setExternalIds(externalIds);

        FactomResponse<ComposeResponse> response = client.composeEntry(entry, EC_PUBLIC_KEY);
        assertValidResponse(response);

        ComposeResponse composeResponse = response.getResult();
        Assert.assertNotNull(composeResponse.getCommit());
        Assert.assertNotNull(composeResponse.getCommit().getId());
        Assert.assertNotNull(composeResponse.getCommit().getParams());
        Assert.assertNotNull(composeResponse.getCommit().getParams().getMessage());
        Assert.assertNotNull(composeResponse.getReveal());
        Assert.assertNotNull(composeResponse.getReveal().getId());
        Assert.assertNotNull(composeResponse.getReveal().getParams());
        Assert.assertNotNull(composeResponse.getReveal().getParams().getEntry());
    }

    @Test
    public void _41_getHeight() throws FactomException.ClientException {
        FactomResponse<GetHeightResponse> response = client.getHeight();
        assertValidResponse(response);

        GetHeightResponse heightResponse = response.getResult();
        Assert.assertNotNull(heightResponse);
        height = heightResponse.getHeight();
        Assert.assertTrue(height > 0);
    }

    @Test
    public void _42_transactionsByRange() throws FactomException.ClientException {
        int start = height - 10;
        int end = height + 10;

        Range range = new Range();
        range.setStart(start);
        range.setEnd(end);

        FactomResponse<BlockHeightTransactionsResponse> response = client.transactionsByRange(range);
        assertValidResponse(response);

        BlockHeightTransactionsResponse transactions = response.getResult();
        Assert.assertNotNull(transactions);
        Assert.assertNotNull(transactions.getTransactions());
        Assert.assertFalse(transactions.getTransactions().isEmpty());

        BlockHeightTransactionResponse transaction = transactions.getTransactions().get(0);
        Assert.assertNotNull(transaction);
        Assert.assertTrue(start <= transaction.getBlockHeight() && end >= transaction.getBlockHeight());
    }

    @Test
    public void walletBackup() throws FactomException.ClientException {
        FactomResponse<WalletBackupResponse> response = client.walletBackup();
        assertValidResponse(response);
        WalletBackupResponse walletBackup = response.getResult();
        Assert.assertNotNull(walletBackup.getWalletSeed());
        Assert.assertNotNull(walletBackup.getAddresses());
        Assert.assertFalse(walletBackup.getAddresses().isEmpty());
    }
}