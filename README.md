# Hadoop Taxi Trip Count

## Gioi thieu
Du an su dung Apache Hadoop HDFS va Java Mapreduce
de dem so chuyen taxi theo ngay tu du lieu CSV.

## Cong nghe
- Ubuntu 22.04 / WSL2
- Java 11
- Apache Hadoop 3.3.6
- HDFS 
- Java Mapreduce

## Ma nguon
Ma Java nam trong thu muc 'src/'.

## Du lieu dau vao
Chuong trinh su dung du lieu NYC Yellow Taxi Trip Data.
Khong bao gom toan bo du lieu trong repository.
Nguoi chay can tu chuan bi CSV va tai len HDFS.

## Bien dich
Tu thu muc du an:

'''bash
mkdir -p build
javac -classpath "$(hadoop classpath)" -d build src/TripCount.java
jar -cvf build/taxi-count.jar -C build.
