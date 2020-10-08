cd "$(dirname "$0")"
nohup mvn spring-boot:run >> /home/ky03/weiboweb/log.txt 2>&1 &
