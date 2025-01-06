## RSA cert
1. openssl genrsa 4096 > ca-private-key-rsa.pem
2. openssl req -new -x509 -nodes -sha512 -days 3652 -key ca-private-key-rsa.pem -out ca-certificate-rsa.cer
3. openssl pkcs12 -inkey ca-private-key-rsa.pem -in ca-certificate-rsa.cer -name key -export -out ca-keystore-rsa.p12

Country=EE
State or Province Name (full name) [Some-State]:Harjumaa
Locality Name (eg, city) []:Tallinn
Org=Selenide
Org unit=Selenide Proxy RSA Impersonation CA
CN=Selenide Proxy MITM
Email Address []:andrei.solntsev+selenide+proxy@gmail.com

Password: password

## EC cert
1. openssl ecparam -out ca-private-key-ec.pem -name P-256 -genkey
2. openssl req -new -key ca-private-key-ec.pem -x509 -nodes -sha512 -days 3652 -out ca-certificate-ec.cer
3. openssl pkcs12 -inkey ca-private-key-ec.pem -in ca-certificate-ec.cer -name key -export -out ca-keystore-ec.p12

Country=EE
State or Province Name (full name) [Some-State]:Harjumaa
Locality Name (eg, city) []:Tallinn
Org=Selenide
Org unit=Selenide Proxy EC Impersonation CA
CN=Selenide Proxy MITM
Email Address []:andrei.solntsev+selenide+proxy@gmail.com

Password: password


-----------
openssl pkcs12 -info -in ca-keystore-ec.p12                                                     
Enter Import Password:
MAC: sha256, Iteration 2048
MAC length: 32, salt length: 8
PKCS7 Encrypted data: PBES2, PBKDF2, AES-256-CBC, Iteration 2048, PRF hmacWithSHA256
Certificate bag
Bag Attributes
friendlyName: key
localKeyID: AF 3C 68 A3 72 85 91 37 B7 94 A3 AF 36 01 A9 D3 84 01 3C F2
subject=C=EE, ST=Harjumaa, L=Tallinn, O=Selenide, OU=Selenide Proxy EC Impersonation CA, CN=Selenide Proxy MITM, emailAddress=andrei.solntsev+selenide+proxy@gmail.com
issuer=C=EE, ST=Harjumaa, L=Tallinn, O=Selenide, OU=Selenide Proxy EC Impersonation CA, CN=Selenide Proxy MITM, emailAddress=andrei.solntsev+selenide+proxy@gmail.com
-----BEGIN CERTIFICATE-----
MIIDiDCCAt6gAwIBAgIUO7DUqEKN8qXqSw3nBZ/qqBy4xbMwCgYIKoZIzj0EAwQw
gckxCzAJBgNVBAYTAkVFMREwDwYDVQQIDAhIYXJqdW1hYTEQMA4GA1UEBwwHVGFs
bGlubjERMA8GA1UECgwIU2VsZW5pZGUxKzApBgNVBAsMIlNlbGVuaWRlIFByb3h5
IEVDIEltcGVyc29uYXRpb24gQ0ExHDAaBgNVBAMME1NlbGVuaWRlIFByb3h5IE1J
VE0xNzA1BgkqhkiG9w0BCQEWKGFuZHJlaS5zb2xudHNlditzZWxlbmlkZStwcm94
eUBnbWFpbC5jb20wHhcNMjUwMTA2MTYxNjI4WhcNMzUwMTA2MTYxNjI4WjCByTEL
MAkGA1UEBhMCRUUxETAPBgNVBAgMCEhhcmp1bWFhMRAwDgYDVQQHDAdUYWxsaW5u
MREwDwYDVQQKDAhTZWxlbmlkZTErMCkGA1UECwwiU2VsZW5pZGUgUHJveHkgRUMg
SW1wZXJzb25hdGlvbiBDQTEcMBoGA1UEAwwTU2VsZW5pZGUgUHJveHkgTUlUTTE3
MDUGCSqGSIb3DQEJARYoYW5kcmVpLnNvbG50c2V2K3NlbGVuaWRlK3Byb3h5QGdt
YWlsLmNvbTCBpzAQBgcqhkjOPQIBBgUrgQQAJwOBkgAEAnIwWAQ5UO9aEg0vWBCM
5Xxw3PjKysoQwn7Ot1UKM/a+PsVv974KZU73R3b+ATbWSsmZcAwCQoGbB6kwAUxH
NMQkw4Z3kE5JBOx+f6oJD6C47PSkiMjvxE0zNISF8p7LqxIwka8r354Rj3nZacHS
UVFjdtBONYQbiDde+AvaS3vhNU31MxhQF5bpRGYY784+o1MwUTAdBgNVHQ4EFgQU
Zk0Q4qI6e2yAK9Vz4e64BHSyFNQwHwYDVR0jBBgwFoAUZk0Q4qI6e2yAK9Vz4e64
BHSyFNQwDwYDVR0TAQH/BAUwAwEB/zAKBggqhkjOPQQDBAOBlwAwgZMCSAPwjsdt
BwVwVe/6L/vsEKkfYM4OBzWS7bU63Gjl0cFHJx0/76vdeYRkBTZScHvIgvc6PrzO
O1tb75eWvqZjj2O3JmSUzbOA/QJHYF+EE3JsFDb2rr2eccyilTPS69plsqY4gQus
xSrSNWPF6sP4KwfAVWAUYXLqEGNquo6c5joir2fkMmqJTYoUl+aVdhv07TQ=
-----END CERTIFICATE-----
PKCS7 Data
Shrouded Keybag: PBES2, PBKDF2, AES-256-CBC, Iteration 2048, PRF hmacWithSHA256
Bag Attributes
friendlyName: key
localKeyID: AF 3C 68 A3 72 85 91 37 B7 94 A3 AF 36 01 A9 D3 84 01 3C F2
Key Attributes: <No Attributes>
Enter PEM pass phrase:
Verifying - Enter PEM pass phrase:
-----BEGIN ENCRYPTED PRIVATE KEY-----
MIIBdTBfBgkqhkiG9w0BBQ0wUjAxBgkqhkiG9w0BBQwwJAQQZzOpTTSX1NzbYrig
SfdGagICCAAwDAYIKoZIhvcNAgkFADAdBglghkgBZQMEASoEEPuEnsD8DyJxoV/s
NMagNs4EggEQa3ecUA6JerKycSFfcArJIK6NMzIFQ5zEChsA5yXQ9dL3hAbWBrQm
e0IjbXfIKM4LQmHcE7R4UD689mJpvJ6iTY8ItXlPUVUY7GKihZtCguCCh+9a0SEb
nBt+CJ0D9XOZCe/IlOpaM5FV8zQy1cwf9QIgTdVOUN7Kcn0kZ+xi+/BKAVU+mABW
0wlTdGeqqFH9uL3CbYDvyRnPHlDETzdicFcoshQ52eqIga+EAq13gesj3pk4Jyq1
63GCqbUVqrUMmbACMfCeMplT0/45MVyAFYC3A35/KEchkHciaoCJ/ACyzcUgebb9
vQfJcxFHR3SG0fAkxbzHi4K2T+4mZRndIMnGRzOCSe1J8Io3aCQGfBA=
-----END ENCRYPTED PRIVATE KEY-----
