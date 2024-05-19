
# Client/Asset-network

# Distributed Student Certification
A hyperledger fabric network to demonstrate client/asset creation, update, transfer and delete functions. 

## Fabric Network
- 1 Organization (csjmu)
- TLS enabled
- 3 Peers

## Chaincode Functionality (Password Protected)
- Two chaincodes( Client and Asset)
- Create a client/asset
- Update
- Transfer
- Read
- Delete

## Network Setup

1. Pre-setup
    1. Generate Crypto Materials, Docker Network Setup
		```console
        hyper@DESKTOP-M9UTCH0:~$ ./network.sh down
        hyper@DESKTOP-M9UTCH0:~$ ./network.sh up
	
	2. Generate Channel Artifacts
	    ```console
        hyper@DESKTOP-M9UTCH0:~$ ./network.sh createChannel
2. Install & Instantiate Chaincode
	1. Deploy chaincode (Asset)
	    ```console
        hyper@DESKTOP-M9UTCH0:~$ ./network.sh deployCC -ccn basic -ccp ../chaincode/asset-java -ccl java

     2. Deploy chaincode (Client)
	    ```console
        hyper@DESKTOP-M9UTCH0:~$ ./network.sh deployCC -ccn basic -ccp ../chaincode/client-java -ccl java

3. Interacting with the network
	1. Setting up invironment
	    ```console
        hyper@DESKTOP-M9UTCH0:~$ export PATH=${PWD}/../bin:$PATH
        hyper@DESKTOP-M9UTCH0:~$ export FABRIC_CFG_PATH=$PWD/../config/
        hyper@DESKTOP-M9UTCH0:~$ export CORE_PEER_TLS_ENABLED=true
        hyper@DESKTOP-M9UTCH0:~$ export CORE_PEER_LOCALMSPID=Org1MSP
        hyper@DESKTOP-M9UTCH0:~$ export CORE_PEER_TLS_ROOTCERT_FILE=${PWD}/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt
        hyper@DESKTOP-M9UTCH0:~$ export CORE_PEER_MSPCONFIGPATH=${PWD}/organizations/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp
        hyper@DESKTOP-M9UTCH0:~$ export CORE_PEER_ADDRESS=localhost:7051
     
	2. Initializing the ledger with assets
	    ```console
        hyper@DESKTOP-M9UTCH0:~$ peer chaincode invoke -o localhost:7050 --ordererTLSHostnameOverride orderer.csjmu.com --tls --cafile "${PWD}/organizations/ordererOrganizations/csjmu.com/orderers/orderer.csjmu.com/msp/tlscacerts/tlsca.csjmu.com-cert.pem" -C mychannel -n basic --peerAddresses localhost:7051 --tlsRootCertFiles "${PWD}/organizations/peerOrganizations/csjmu.com/peers/peer0.csjmu.com/tls/ca.crt" --peerAddresses localhost:9051 --tlsRootCertFiles "${PWD}/organizations/peerOrganizations/csjmu.com/peers/peer1.csjmu.com/tls/ca.crt" --peerAddresses localhost:11051 --tlsRootCertFiles "${PWD}/organizations/peerOrganizations/csjmu.com/peers/peer2.csjmu.com/tls/ca.crt" -c '{"function":"InitLedger","Args":[]}'
		
	3. Output will be
		```console
        -> INFO 001 Chaincode invoke successful. result: status:200
	4. To read all asset
        ```console
        hyper@DESKTOP-M9UTCH0:~$ peer chaincode query -C mychannel -n basic -c '{"Args":["GetAllAssets"]}'
        
    5. Invoking the asset-transfer (basic) chaincode
        ```console
        hyper@DESKTOP-M9UTCH0:~$ peer chaincode invoke -o localhost:7050 --ordererTLSHostnameOverride orderer.csjmu.com --tls --cafile "${PWD}/organizations/ordererOrganizations/csjmu.com/orderers/orderer.csjmu.com/msp/tlscacerts/tlsca.csjmu.com-cert.pem" -C mychannel -n basic --peerAddresses localhost:7051 --tlsRootCertFiles "${PWD}/organizations/peerOrganizations/csjmu.com/peers/peer0.csjmu.com/tls/ca.crt" --peerAddresses localhost:9051 --tlsRootCertFiles "${PWD}/organizations/peerOrganizations/csjmu.com/peers/peer1.csjmu.com/tls/ca.crt" --peerAddresses localhost:11051 --tlsRootCertFiles "${PWD}/organizations/peerOrganizations/csjmu.com/peers/peer2.csjmu.com/tls/ca.crt" -c'{"function":"ReadAsset","Args":["asset6","Alok@123"]}'
    You can invoke chaincode for different functions like create, read, update, initiate, transfer, delete ect. every functions have their own working and protection criteria.

4. View Container Logs
	1. View all containers
	    ```console
		hyper@DESKTOP-M9UTCH0:~$ docker ps -a

     2. View container logs
	    ```console
		hyper@DESKTOP-M9UTCH0:~$ docker logs <containerid>


