/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.assettransfer;

import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType()
public final class Client {

    @Property()
    private final String clientID;

    @Property()
    private final String owner;

    @Property()
    private final String password;

    @Property()
    private final int tender_rate;

    @Property()
    private final String tender_data;

    private String adminpswd;

    private String adminID;

    public String getAdminID() {
        return adminID;
    }
    public String getAdminpswd() {
        return adminpswd;
    }

    public String getClientID() {
        return clientID;
    }

    public String getOwner() {
        return owner;
    }

    public String getPassword() {
        return password;
    }

    public int getTender_rate() {
        return tender_rate;
    }

    public String getTender_data() {
        return tender_data;
    }

    public Client(@JsonProperty("clientID") final String clientID, @JsonProperty("owner") final String owner,
            @JsonProperty("password") final String password, @JsonProperty("tender_rate") final int tender_rate,
            @JsonProperty("tender_data") final String tender_data) {
        this.clientID = clientID;
        this.owner = owner;
        this.password = password;
        this.tender_rate = tender_rate;
        this.tender_data = tender_data;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        Client other = (Client) obj;

        return Objects.deepEquals(
                new String[] {getClientID(), getOwner(), getPassword(), getTender_data()},
                new String[] {other.getClientID(), other.getOwner(), other.getPassword(), other.getTender_data()})
                &&
                Objects.deepEquals(
                new int[] {getTender_rate()},
                new int[] {other.getTender_rate()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClientID(), getOwner(), getPassword(), getTender_rate(), getTender_data());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [clientID=" + clientID + ", owner=" + owner + ", password="
                + password + ", tender_rate=" + tender_rate + ", tender_data=" + tender_data + "]";
    }
}
