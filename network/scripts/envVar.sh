#!/bin/bash

# This is a collection of bash functions used by different scripts

# Imports
. scripts/utils.sh

export CORE_PEER_TLS_ENABLED=true
export ORDERER_CA=${PWD}/organizations/ordererOrganizations/csjmu.com/tlsca/tlsca.csjmu.com-cert.pem
export PEER0_CSJMU_CA=${PWD}/organizations/peerOrganizations/csjmu.com/tlsca/tlsca.csjmu.com-cert.pem
export PEER1_CSJMU_CA=${PWD}/organizations/peerOrganizations/csjmu.com/tlsca/tlsca.csjmu.com-cert.pem
export PEER2_CSJMU_CA=${PWD}/organizations/peerOrganizations/csjmu.com/tlsca/tlsca.csjmu.com-cert.pem

# Set environment variables for the peer org
setGlobals() {
  local USING_PEER=""
  if [ -z "$OVERRIDE_PEER" ]; then
    USING_PEER=$1
  else
    USING_PEER="${OVERRIDE_PEER}"
  fi
  infoln "Using organization csjmu"
  if [ $USING_PEER -eq 0 ]; then
    export CORE_PEER_LOCALMSPID="csjmuMSP"
    export CORE_PEER_TLS_ROOTCERT_FILE=$PEER0_CSJMU_CA
    export CORE_PEER_MSPCONFIGPATH=${PWD}/organizations/peerOrganizations/csjmu.com/users/Admin@csjmu.com/msp
    export CORE_PEER_ADDRESS=peer0.csjmu.com:7051
  elif [ $USING_PEER -eq 1 ]; then
    export CORE_PEER_LOCALMSPID="csjmuMSP"
    export CORE_PEER_TLS_ROOTCERT_FILE=$PEER1_CSJMU_CA
    export CORE_PEER_MSPCONFIGPATH=${PWD}/organizations/peerOrganizations/csjmu.com/users/Admin@csjmu.com/msp
    export CORE_PEER_ADDRESS=peer1.csjmu.com:9051
  elif [ $USING_PEER -eq 2 ]; then
    export CORE_PEER_LOCALMSPID="csjmuMSP"
    export CORE_PEER_TLS_ROOTCERT_FILE=$PEER2_CSJMU_CA
    export CORE_PEER_MSPCONFIGPATH=${PWD}/organizations/peerOrganizations/csjmu.com/users/Admin@csjmu.com/msp
    export CORE_PEER_ADDRESS=peer2.csjmu.com:11051
  else
    errorln "Unknown PEER"
  fi

  if [ "$VERBOSE" == "true" ]; then
    env | grep CORE
  fi
}

# Set environment variables for use in the CLI container
setGlobalsCLI() {
  setGlobals $1
  if [ $1 -eq 0 ]; then
    export CORE_PEER_ADDRESS=peer0.csjmu.com:7051
  elif [ $1 -eq 1 ]; then
    export CORE_PEER_ADDRESS=peer1.csjmu.com:9051
  elif [ $1 -eq 2 ]; then
    export CORE_PEER_ADDRESS=peer2.csjmu.com:11051
  else
    errorln "Unknown PEER"
  fi
}

# parsePeerConnectionParameters $@
# Helper function that sets the peer connection parameters for a chaincode operation
parsePeerConnectionParameters() {
  PEER_CONN_PARMS=()
  PEERS=""
  while [ "$#" -gt 0 ]; do
    setGlobals $1
    PEER="peer$1.csjmu.com"
    ## Set peer addresses
    if [ -z "$PEERS" ]; then
      PEERS="$PEER"
    else
      PEERS="$PEERS $PEER"
    fi
    PEER_CONN_PARMS=("${PEER_CONN_PARMS[@]}" --peerAddresses $CORE_PEER_ADDRESS)
    ## Set path to TLS certificate
    CA=PEER${1}_CSJMU_CA
    TLSINFO=(--tlsRootCertFiles "${!CA}")
    PEER_CONN_PARMS=("${PEER_CONN_PARMS[@]}" "${TLSINFO[@]}")
    # shift by one to get to the next organization
    shift
  done
}

verifyResult() {
  if [ $1 -ne 0 ]; then
    fatalln "$2"
  fi
}
