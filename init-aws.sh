# Create Table
aws --endpoint="http://localhost:4566" dynamodb create-table \
  --region "us-east-1" \
  --table-name "pagamento_transacao" \
  --attribute-definitions \
    "AttributeName=idCliente,AttributeType=S" \
    "AttributeName=idCarrinho,AttributeType=S" \
  --key-schema \
    "AttributeName=idCliente,KeyType=HASH" \
    "AttributeName=idCarrinho,KeyType=RANGE" \
  --provisioned-throughput \
      "ReadCapacityUnits=5,WriteCapacityUnits=5"
