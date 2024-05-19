#!/bin/bash
#
# SPDX-License-Identifier: Apache-2.0



# output the variables that need to be set
echo "GRANTING PERMISSION FOR NETWORK UP"

chmod +x network.sh

chmod +x organizations/cryptogen/crypto-config.yaml

chmod +x organizations/ccp-generate.sh
chmod +x organizations/ccp-template.json
chmod +x organizations/ccp-template.yaml

chmod +x compose/compose-test-net.yaml
chmod +x compose/docker/docker-compose-test-net.yaml

chmod +x configtx/configtx.yaml

chmod +x scripts/createChannel.sh
chmod +x scripts/ccutils.sh
chmod +x scripts/utils.sh
chmod +x scripts/envVar.sh
chmod +x scripts/orderer.sh
chmod +x scripts/setAnchorPeer.sh
chmod +x scripts/configUpdate.sh
chmod +x scripts/deployCC.sh
