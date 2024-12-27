# Some commands that I used to start Moon on Mac M1.
# Not sure that this is a full and up-to-date script.
# See https://aerokube.com/moon/latest/#getting-started

helm repo add aerokube https://charts.aerokube.com/
helm repo update

minikube start --cpus=6 --memory=6G --disk-size=20G --driver qemu --network socket_vmnet
helm upgrade --install -f moon-values.yaml -n moon moon aerokube/moon2
minikube addons enable ingress

kubectl patch svc moon -n moon --patch "{\"spec\":{\"externalIPs\":[\"$(minikube ip)\"]}}"
minikube ip -- add to /etc/hosts as "moon.aerokube.local"
