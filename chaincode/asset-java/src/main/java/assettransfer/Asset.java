

package org.hyperledger.fabric.samples.assettransfer;

import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType()
public final class Asset {

    @Property()
    private final String assetID;

    @Property()
    private final String owner;

    @Property()
    private final String password;

    @Property()
    private final int account_number;

    @Property()
    private final String data;

    private String adminpswd;

    private String adminID;

    public String getAdminID() {
        return adminID;
    }
    public String getAdminpswd() {
        return adminpswd;
    }

    public String getAssetID() {
        return assetID;
    }

    public String getOwner() {
        return owner;
    }

    public String getPassword() {
        return password;
    }

    public int getAccount_number() {
        return account_number;
    }

    public String getData() {
        return data;
    }

    public Asset(@JsonProperty("assetID") final String assetID, @JsonProperty("owner") final String owner,
            @JsonProperty("password") final String password, @JsonProperty("account_number") final int account_number,
            @JsonProperty("data") final String data) {
        this.assetID = assetID;
        this.owner = owner;
        this.password = password;
        this.account_number = account_number;
        this.data = data;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        Asset other = (Asset) obj;

        return Objects.deepEquals(
                new String[] {getAssetID(), getOwner(), getPassword(), getData()},
                new String[] {other.getAssetID(), other.getOwner(), other.getPassword(), other.getData()})
                &&
                Objects.deepEquals(
                new int[] {getAccount_number()},
                new int[] {other.getAccount_number()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAssetID(), getOwner(), getPassword(), getAccount_number(), getData());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [assetID=" + assetID + ", owner=" + owner + ", password="
                + password + ", account_number=" + account_number + ", data=" + data + "]";
    }
}
