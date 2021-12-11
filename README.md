# BTC-ETH Converter

The BTC-ETH converter is a web service. The BTC-ETH converter converts the number of bitcoin coins to ethereum coins. The API of the coinmarketcap.com service is used.

## Requirements
- Java 16

## Build the project
### Windows:
`gradlew.bat build`
### Linux:
`./gradlew build`

The build artifact is located in `project_dir/build/libs/`

## Service start
`java -jar btc-eth-converter-1.0-SNAPSHOT.jar port` 
<br>You must specify a port.
<br>You need to create an api_key.txt file with the coinmarketcap.com API access key.

## Running in Docker

### Build
`docker build -t btc-eth-converter .`

### Run
`docker run -d -v $(pwd)/api_key.txt:/btc_eth_converter/api_key.txt -p 8000:8000 btc-eth-converter`

## Usage example
`curl http://127.0.0.1:8000/api/convert?amount=10`
