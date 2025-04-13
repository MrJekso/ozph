Instructions to run
1. cd ozph
2. javac -d app src/Main.java
3. cd app
4. jar cfe Main.jar Main Main.class OZPH.class Main$Server.class
5. cd ..
6. docker build . -t ozph:version
7. docker run -d -p 9999:9999 --name ozph ozph:version
8. docker run -d -p 9090:9090 --name prometheus bitnami/prometheus:latest
9. docker inspect ozph #see ip
10. edit file prometheus.yml set host and port ozph container
11. docker cp prometheus.yml prometheus:/opt/bitnami/prometheus/conf/prometheus.yml
12. docker stop prometheus
13. docker start prometheus
