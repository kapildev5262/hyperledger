
package org.hyperledger.fabric.samples.assettransfer;

import java.util.ArrayList;
import java.util.List;


import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import com.owlike.genson.Genson;

@Contract(
        name = "basic",
        info = @Info(
                title = "Client Transfer",
                description = "The hyperlegendary asset transfer",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "a.transfer@example.com",
                        name = "Adrian Transfer",
                        url = "https://hyperledger.example.com")))
@Default
public final class ClientTransfer implements ContractInterface {

    private final Genson genson = new Genson();

    private enum ClientTransferErrors {
        CLIENT_NOT_FOUND,
        CLIENT_ALREADY_EXISTS,
        WRONG_PASSWORD,
        ID_IS_WRONG
    }

    /**
     * Creates some initial assets on the ledger.
     *
     * @param ctx the transaction context
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void InitLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        Create(ctx, "client1", "Kapil", "Kapil@123", 12000, "Adhar-542362348452,tender-mess, tender_duration-5years");
        Create(ctx, "client2", "Rahul", "Rahul@123", 12500, "Adhar-542362348452,tender-mess, tender_duration-5years");
        Create(ctx, "client3", "Navneet", "Navneet@123", 11000, "Adhar-542362348452,tender-mess, tender_duration-5years");
        Create(ctx, "client4", "Pooja", "Pooja123", 11500, "Adhar-542362348452,tender-mess, tender_duration-5years");
        Create(ctx, "client5", "Alok", "Alok@123", 14000, "Adhar-542362348452,tender-mess, tender_duration-5years");

    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Client Create(final Context ctx, final String clientID, final String owner, final String password,
                             final int tender_rate, final String tender_data) {
        ChaincodeStub stub = ctx.getStub();

        if (ClientExists(ctx, clientID)) {
            String errorMessage = String.format("Asset %s already exists", clientID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, ClientTransferErrors.CLIENT_ALREADY_EXISTS.toString());
        }

        Client client = new Client(clientID, owner, password, tender_rate, tender_data);
        // Use Genson to convert the Asset into string, sort it alphabetically and serialize it into a json string
        String sortedJson = genson.serialize(client);
        stub.putStringState(clientID, sortedJson);

        return client;
    }


    /**
     * Retrieves an Client with the specified ID from the ledger.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)

    public Client ReadClient(final Context ctx, final String clientID, final String password) {
        ChaincodeStub stub = ctx.getStub();
        String clientJSON = stub.getStringState(clientID);

        if (clientJSON == null || clientJSON.isEmpty()) {
            String errorMessage = String.format("Client %s does not exist", clientID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, ClientTransferErrors.CLIENT_NOT_FOUND.toString());
        }
        if (!ValidatePassword(ctx, clientID, password)) {
            String errorMessage = String.format("Wrong password for %s", clientID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, ClientTransferErrors.WRONG_PASSWORD.toString());
        }

        Client client = genson.deserialize(clientJSON, Client.class);
        return client;
    }


    /**
     * Deletes client on the ledger.
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)

    public void DeleteClient(final Context ctx, final String clientID, final String adminID, final String adminpswd) {
        ChaincodeStub stub = ctx.getStub();


        if (!ClientExists(ctx, clientID)) {
            String errorMessage = String.format("Client %s does not exist", clientID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, ClientTransferErrors.CLIENT_NOT_FOUND.toString());
        }
        if (!(adminID.equals("adminID"))) {
            String errorMessage = String.format("Admin ID is wrong");
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, ClientTransferErrors.ID_IS_WRONG.toString());
        } else if (!(adminpswd.equals("Admin@123"))) {
            String errorMessage = String.format("Admin password is wrong");
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, ClientTransferErrors.WRONG_PASSWORD.toString());
        }

        stub.delState(clientID);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Client InitiateTender(final Context ctx, final String clientID, final String password, final String newTender_data) {
        ChaincodeStub stub = ctx.getStub();
        String clientJSON = stub.getStringState(clientID);

        if (clientJSON == null || clientJSON.isEmpty()) {
            String errorMessage = String.format("Client %s does not exist", clientID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, ClientTransferErrors.CLIENT_NOT_FOUND.toString());
        }
        if (!ValidatePassword(ctx, clientID, password)) {
            String errorMessage = String.format("Wrong password for %s", clientID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, ClientTransferErrors.WRONG_PASSWORD.toString());
        }

        Client client = genson.deserialize(clientJSON, Client.class);

        Client newClient = new Client(client.getClientID(), client.getOwner(), client.getPassword(), client.getTender_rate(), newTender_data);
        // Use a Genson to conver the client into string, sort it alphabetically and serialize it into a json string
        String sortedJson = genson.serialize(newClient);
        stub.putStringState(clientID, sortedJson);

        return newClient;
    }

    /**
     * Checks the existence of the asset on the ledger
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)

    public boolean ClientExists(final Context ctx, final String clientID) {
        ChaincodeStub stub = ctx.getStub();
        String assetJSON = stub.getStringState(clientID);

        return (assetJSON != null && !assetJSON.isEmpty());
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean ValidatePassword(final Context ctx, final String clientID, final String password) {
        ChaincodeStub stub = ctx.getStub();
        String clientJSON = stub.getStringState(clientID);
        Client client = genson.deserialize(clientJSON, Client.class);

        return password.equals(client.getPassword());
    }


    /**
     * Retrieves all assets from the ledger.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String GetAllClients(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        List<Client> queryResults = new ArrayList<Client>();

        // To retrieve all assets from the ledger use getStateByRange with empty startKey & endKey.
        // Giving empty startKey & endKey is interpreted as all the keys from beginning to end.
        // As another example, if you use startKey = 'asset0', endKey = 'asset9' ,
        // then getStateByRange will retrieve asset with keys between asset0 (inclusive) and asset9 (exclusive) in lexical order.
        QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");

        for (KeyValue result: results) {
            Client client = genson.deserialize(result.getStringValue(), Client.class);
            System.out.println(client);
            queryResults.add(client);
        }

        final String response = genson.serialize(queryResults);

        return response;
    }
}
