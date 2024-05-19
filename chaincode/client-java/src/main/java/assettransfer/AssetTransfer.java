/*
 * SPDX-License-Identifier: Apache-2.0
 */

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
                title = "Asset Transfer",
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
public final class AssetTransfer implements ContractInterface {

    private final Genson genson = new Genson();

    private enum AssetTransferErrors {
        ASSET_NOT_FOUND,
        ASSET_ALREADY_EXISTS,
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

        Create(ctx, "asset1", "Parul", "Admin@123", 123451, "1000");
        Create(ctx, "asset2", "Kapil", "Kapil@123", 123452, "800");
        Create(ctx, "asset3", "Rahul", "Rahul@123", 123453, "Transaction Id:RahCSJM1261, Validation  Code:VALID, Payload Proposal: Rahul1261, Creator MSP :Csjmu1MSP, Endoser:{ Csjmu1MSP,Csjmu2MSP,Csjmu3MSP,Csjmu4MSP,Csjmu5MSP, Csjmu6MSP, ,Csjmu7MSP}, Chaincode name : Project chain, Type: CLIENT TRANSACTION");
        Create(ctx, "asset4", "Navneet", "Navneet@123", 123454, "600");
        Create(ctx, "asset5", "Pooja", "Pooja123", 123455, "700");
        Create(ctx, "asset6", "Alok", "Alok@123", 123456, "600");

    }

    /**
     * Creates a new asset on the ledger.
     *
     * @param ctx the transaction context
     * @param assetID the ID of the new asset
     * @param color the color of the new asset
     * @param size the size for the new asset
     * @param owner the owner of the new asset
     * @param appraisedValue the appraisedValue of the new asset
     * @return the created asset
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Asset Create(final Context ctx, final String assetID, final String owner, final String password,
                             final int account_number, final String data) {
        ChaincodeStub stub = ctx.getStub();

        if (AssetExists(ctx, assetID)) {
            String errorMessage = String.format("Asset %s already exists", assetID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_ALREADY_EXISTS.toString());
        }

        Asset asset = new Asset(assetID, owner, password, account_number, data);
        // Use Genson to convert the Asset into string, sort it alphabetically and serialize it into a json string
        String sortedJson = genson.serialize(asset);
        stub.putStringState(assetID, sortedJson);

        return asset;
    }

//    @Transaction(intent = Transaction.TYPE.SUBMIT)
//
//    public Asset CreateAsset(final Context ctx, final String assetID, final String owner, final String password,
//        final int account_number, final int appraisedValue, final String adminpswd) {
//        ChaincodeStub stub = ctx.getStub();
//
//        if (AssetExists(ctx, assetID)) {
//            String errorMessage = String.format("Asset %s already exists", assetID);
//            System.out.println(errorMessage);
//            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_ALREADY_EXISTS.toString());
//        }
//
//        if (!(adminpswd.equals("Admin@123"))) {
//            String errorMessage = String.format("Admin password is wrong");
//            System.out.println(errorMessage);
//            throw new ChaincodeException(errorMessage, AssetTransferErrors.WRONG_PASSWORD.toString());
//        }
//
//        Asset asset = new Asset(assetID, owner, password, account_number, appraisedValue);
//        // Use Genson to convert the Asset into string, sort it alphabetically and serialize it into a json string
//        String sortedJson = genson.serialize(asset);
//        stub.putStringState(assetID, sortedJson);
//
//        return asset;
//    }

    /**
     * Retrieves an asset with the specified ID from the ledger.
     *
     * @param ctx the transaction context
     * @param assetID the ID of the asset
     * @return the asset found on the ledger if there was one
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Asset ReadAsset(final Context ctx, final String assetID, final String password) {
        ChaincodeStub stub = ctx.getStub();
        String assetJSON = stub.getStringState(assetID);

        if (assetJSON == null || assetJSON.isEmpty()) {
            String errorMessage = String.format("Asset %s does not exist", assetID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_NOT_FOUND.toString());
        }
        if (!ValidatePassword(ctx, assetID, password)) {
            String errorMessage = String.format("Wrong password for %s", assetID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.WRONG_PASSWORD.toString());
        }

        Asset asset = genson.deserialize(assetJSON, Asset.class);
        return asset;
    }

    /**
     * Updates the properties of an asset on the ledger.
     *
     * @param ctx the transaction context
     * @param assetID the ID of the asset being updated
     * @param color the color of the asset being updated
     * @param size the size of the asset being updated
     * @param owner the owner of the asset being updated
     * @param appraisedValue the appraisedValue of the asset being updated
     * @return the transferred asset
     */

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Asset InitiateAsset(final Context ctx, final String assetID, final String password, final String newData) {
        ChaincodeStub stub = ctx.getStub();
        String assetJSON = stub.getStringState(assetID);

        if (assetJSON == null || assetJSON.isEmpty()) {
            String errorMessage = String.format("Asset %s does not exist", assetID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_NOT_FOUND.toString());
        }
        if (!ValidatePassword(ctx, assetID, password)) {
            String errorMessage = String.format("Password is wrong for %s", assetID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.WRONG_PASSWORD.toString());
        }

        Asset asset = genson.deserialize(assetJSON, Asset.class);

        Asset newAsset = new Asset(asset.getAssetID(), asset.getOwner(), asset.getPassword(), asset.getAccount_number(), newData);
        // Use a Genson to conver the Asset into string, sort it alphabetically and serialize it into a json string
        String sortedJson = genson.serialize(newAsset);
        stub.putStringState(assetID, sortedJson);

        return newAsset;
    }


    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Asset UpdateAsset(final Context ctx, final String assetID, final String owner, final String password,
        final int account_number, final String data) {
        ChaincodeStub stub = ctx.getStub();

        if (!AssetExists(ctx, assetID)) {
            String errorMessage = String.format("Asset %s does not exist", assetID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_NOT_FOUND.toString());
        }
        if (!ValidatePassword(ctx, assetID, password)) {
            String errorMessage = String.format("Wrong password for %s", assetID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.WRONG_PASSWORD.toString());
        }

        Asset newAsset = new Asset(assetID, owner, password, account_number, data);
        // Use Genson to convert the Asset into string, sort it alphabetically and serialize it into a json string
        String sortedJson = genson.serialize(newAsset);
        stub.putStringState(assetID, sortedJson);
        return newAsset;
    }

    /**
     * Deletes asset on the ledger.
     *
     * @param ctx the transaction context
     * @param assetID the ID of the asset being deleted
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void DeleteAsset(final Context ctx, final String assetID, final String adminID, final String adminpswd) {
        ChaincodeStub stub = ctx.getStub();


        if (!AssetExists(ctx, assetID)) {
            String errorMessage = String.format("Asset %s does not exist", assetID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_NOT_FOUND.toString());
        }
        if (!(adminID.equals("asset1"))) {
            String errorMessage = String.format("Admin ID is wrong");
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ID_IS_WRONG.toString());
        } else if (!(adminpswd.equals("Admin@123"))) {
            String errorMessage = String.format("Admin password is wrong");
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.WRONG_PASSWORD.toString());
        }

        stub.delState(assetID);
    }

    /**
     * Checks the existence of the asset on the ledger
     *
     * @param ctx the transaction context
     * @param assetID the ID of the asset
     * @return boolean indicating the existence of the asset
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean AssetExists(final Context ctx, final String assetID) {
        ChaincodeStub stub = ctx.getStub();
        String assetJSON = stub.getStringState(assetID);

        return (assetJSON != null && !assetJSON.isEmpty());
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean ValidatePassword(final Context ctx, final String assetID, final String password) {
        ChaincodeStub stub = ctx.getStub();
        String assetJSON = stub.getStringState(assetID);
        Asset asset = genson.deserialize(assetJSON, Asset.class);

        return password.equals(asset.getPassword());
    }

    /**
     * Changes the owner of a asset on the ledger.
     *
     * @param ctx the transaction context
     * @param assetID the ID of the asset being transferred
     * @param newOwner the new owner
     * @return the old owner
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String TransferAsset(final Context ctx, final String assetID, final String password, final String newOwner, final String newPassword) {
        ChaincodeStub stub = ctx.getStub();
        String assetJSON = stub.getStringState(assetID);

        if (assetJSON == null || assetJSON.isEmpty()) {
            String errorMessage = String.format("Asset %s does not exist", assetID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_NOT_FOUND.toString());
        }
        if (!ValidatePassword(ctx, assetID, password)) {
            String errorMessage = String.format("Password is wrong for %s", assetID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.WRONG_PASSWORD.toString());
        }

        Asset asset = genson.deserialize(assetJSON, Asset.class);

        Asset newAsset = new Asset(asset.getAssetID(), newOwner, newPassword, asset.getAccount_number(), asset.getData());
        // Use a Genson to conver the Asset into string, sort it alphabetically and serialize it into a json string
        String sortedJson = genson.serialize(newAsset);
        stub.putStringState(assetID, sortedJson);

        return asset.getOwner();
    }

    /**
     * Retrieves all assets from the ledger.
     *
     * @param ctx the transaction context
     * @return array of assets found on the ledger
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String GetAllAssets(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        List<Asset> queryResults = new ArrayList<Asset>();

        // To retrieve all assets from the ledger use getStateByRange with empty startKey & endKey.
        // Giving empty startKey & endKey is interpreted as all the keys from beginning to end.
        // As another example, if you use startKey = 'asset0', endKey = 'asset9' ,
        // then getStateByRange will retrieve asset with keys between asset0 (inclusive) and asset9 (exclusive) in lexical order.
        QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");

        for (KeyValue result: results) {
            Asset asset = genson.deserialize(result.getStringValue(), Asset.class);
            System.out.println(asset);
            queryResults.add(asset);
        }

        final String response = genson.serialize(queryResults);

        return response;
    }
}
