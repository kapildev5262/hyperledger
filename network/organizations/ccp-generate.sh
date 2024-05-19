#!/bin/bash

function one_line_pem {
    echo "`awk 'NF {sub(/\\n/, ""); printf "%s\\\\\\\n",$0;}' $1`"
}

function json_ccp {
    local PEER=$1
    local P0PORT=$2
    local CAPORT=$3
    local PEERPEM=$(one_line_pem $4)
    local CAPEM=$(one_line_pem $5)
    local ORDERERPEM=$(one_line_pem $6)
    sed -e "s/\${PEER}/$PEER/" \
        -e "s/\${P0PORT}/$P0PORT/" \
        -e "s/\${CAPORT}/$CAPORT/" \
        -e "s#\${PEERPEM}#$PEERPEM#" \
        -e "s#\${CAPEM}#$CAPEM#" \
        -e "s#\${ORDERERPEM}#$ORDERERPEM#" \
        organizations/ccp-template.json
}

function yaml_ccp {
    local PEER=$1
    local P0PORT=$2
    local CAPORT=$3
    local PEERPEM=$(one_line_pem $4)
    local CAPEM=$(one_line_pem $5)
    local ORDERERPEM=$(one_line_pem $6)
    sed -e "s/\${PEER}/$PEER/" \
        -e "s/\${P0PORT}/$P0PORT/" \
        -e "s/\${CAPORT}/$CAPORT/" \
        -e "s#\${PEERPEM}#$PEERPEM#" \
        -e "s#\${CAPEM}#$CAPEM#" \
        -e "s#\${ORDERERPEM}#$ORDERERPEM#" \
        organizations/ccp-template.yaml | sed -e $'s/\\\\n/\\\n          /g'
}

# Define the organization name
ORG=csjmu

# Peer configurations
declare -A PEERS
PEERS["peer0"]=7051
PEERS["peer1"]=9051
PEERS["peer2"]=11051

# CA port
CAPORT=7054

# PEM files
PEERPEM=organizations/peerOrganizations/csjmu.com/tlsca/tlsca.csjmu.com-cert.pem
CAPEM=organizations/peerOrganizations/csjmu.com/ca/ca.csjmu.com-cert.pem
ORDERERPEM=organizations/ordererOrganizations/csjmu.com/tlsca/tlsca.csjmu.com-cert.pem

for PEER in "${!PEERS[@]}"; do
    P0PORT=${PEERS[$PEER]}
    echo "$(json_ccp $PEER $P0PORT $CAPORT $PEERPEM $CAPEM $ORDERERPEM)" > organizations/peerOrganizations/csjmu.com/connection-${PEER}.json
    echo "$(yaml_ccp $PEER $P0PORT $CAPORT $PEERPEM $CAPEM $ORDERERPEM)" > organizations/peerOrganizations/csjmu.com/connection-${PEER}.yaml
done
