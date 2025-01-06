## RSA cert
1. openssl genrsa 4096 > ca-private-key-rsa.pem
2. openssl req -new -x509 -nodes -sha512 -days 3652 -key ca-private-key-rsa.pem -out ca-certificate-rsa.cer \
  -subj "/C=EE/ST=Harjumaa/L=Tallinn/O=Selenide/OU=Selenide Proxy RSA Impersonation CA/CN=Selenide Proxy MITM"
3. openssl pkcs12 -inkey ca-private-key-rsa.pem -in ca-certificate-rsa.cer -name key -export -out ca-keystore-rsa.p12 -password pass:password

## EC cert
1. openssl ecparam -out ca-private-key-ec.pem -name P-256 -genkey
2. openssl req -new -key ca-private-key-ec.pem -x509 -nodes -sha512 -days 3652 -out ca-certificate-ec.cer \
   -subj "/C=EE/ST=Harjumaa/L=Tallinn/O=Selenide/OU=Selenide Proxy EC Impersonation CA/CN=Selenide Proxy MITM"
3. openssl pkcs12 -inkey ca-private-key-ec.pem -in ca-certificate-ec.cer -name key -export -out ca-keystore-ec.p12 -password pass:password
