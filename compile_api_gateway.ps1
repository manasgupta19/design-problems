# Create output directory if it doesn't exist
New-Item -ItemType Directory -Force -Path "bin" | Out-Null

# Compile all Java files into the bin directory
javac -d bin Tier1/ApiGateway/GatewayDriver.java Tier1/ApiGateway/filter/*.java Tier1/ApiGateway/model/*.java Tier1/ApiGateway/service/*.java

Write-Host "Compilation successful. Class files are in 'bin/' directory."
Write-Host "To run: java -cp bin Tier1.ApiGateway.GatewayDriver"
