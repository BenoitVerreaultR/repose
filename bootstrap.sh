sudo yum update -y
sudo mkdir -p /srv/repose
sudo chown ec2-user:ec2-user /srv/repose
cd /srv/repose/
mkdir config
mkdir logs
mkdir newrelic
mkdir deploys
