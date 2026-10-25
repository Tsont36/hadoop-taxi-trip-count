# Hadoop Taxi Trip Count — Đếm chuyến taxi bằng Java MapReduce

## 1. Giới thiệu

Dự án sử dụng Apache Hadoop HDFS và Java MapReduce để đếm số chuyến taxi theo ngày từ dữ liệu NYC Yellow Taxi Trip Data.

Chương trình đọc dữ liệu CSV, lấy ngày đón khách từ trường `tpep_pickup_datetime`, sau đó tổng hợp số chuyến theo từng ngày.

## 2. Công nghệ sử dụng

- Ubuntu 22.04 trên WSL2
- OpenJDK 11
- Apache Hadoop 3.3.6
- Hadoop Distributed File System (HDFS)
- Java MapReduce

## 3. Cấu trúc dự án

```text
hadoop-github/
├── README.md
├── .gitignore
├── src/
│   └── TripCount.java
├── config/
│   ├── core-site.xml
│   ├── hdfs-site.xml
│   └── mapred-site.xml
├── docs/
└── data-sample/
```

## 4. Chuẩn bị dữ liệu

Dự án sử dụng dữ liệu NYC Yellow Taxi Trip Data.

Trang dữ liệu chính thức: https://www.nyc.gov/site/tlc/about/tlc-trip-record-data.page

Dữ liệu csv trong dự án: [https://www.kaggle.com/datasets/elemento/nyc-yellow-taxi-trip-data?select=yellow_tripdata_2016-01.csv](https://www.kaggle.com/datasets/elemento/nyc-yellow-taxi-trip-data?)

Các tệp dữ liệu được sử dụng trong dự án:

- `yellow_tripdata_2015-01.csv`
- `yellow_tripdata_2016-01.csv`
- `yellow_tripdata_2016-02.csv`
- `yellow_tripdata_2016-03.csv`

Các tệp CSV có dung lượng lớn nên **không được lưu trực tiếp trong repository GitHub**. Người chạy cần tải dữ liệu về máy và đưa lên HDFS.

Giả sử các tệp CSV đã được tải vào thư mục `~/taxi-data` trên Ubuntu.

## 5. Đưa dữ liệu lên HDFS

Khởi động SSH và HDFS nếu các dịch vụ chưa chạy:

```bash
sudo service ssh start
start-dfs.sh
```

Tạo thư mục lưu dữ liệu đầu vào:

```bash
hdfs dfs -mkdir -p /BigData/raw/taxi
```

Tải các tệp CSV từ máy Ubuntu lên HDFS:

```bash
hdfs dfs -put ~/taxi-data/*.csv /BigData/raw/taxi/
```

Kiểm tra dữ liệu:

```bash
hdfs dfs -ls /BigData/raw/taxi
```

## 6. Biên dịch chương trình Java

Thực hiện các lệnh sau tại thư mục gốc của repository:

```bash
mkdir -p build/classes
javac -classpath "$(hadoop classpath)" -d build/classes src/TripCount.java
jar -cvf build/taxi-count.jar -C build/classes .
```

Sau khi biên dịch thành công, tệp JAR sẽ nằm tại:

```text
build/taxi-count.jar
```

## 7. Chạy chương trình MapReduce

Chạy lệnh:

```bash
hadoop jar build/taxi-count.jar TripCount \
  /BigData/raw/taxi \
  /BigData/processed/trip-count
```

**Lưu ý:** Thư mục đầu ra `/BigData/processed/trip-count` không được tồn tại trước khi chạy. Nếu đã tồn tại, hãy chọn một đường dẫn đầu ra mới hoặc kiểm tra kết quả cũ trước khi quyết định xóa.

## 8. Xem kết quả

Hiển thị 20 dòng kết quả đầu tiên:

```bash
hdfs dfs -cat /BigData/processed/trip-count/part-r-00000 | head -n 20
```

Đếm số dòng kết quả:

```bash
hdfs dfs -cat /BigData/processed/trip-count/part-r-00000 | wc -l
```

Mỗi dòng kết quả có dạng:

```text
2015-01-01    382014
```

Trong đó, cột đầu tiên là ngày đón khách và cột thứ hai là số chuyến được đếm cho ngày đó.

## 9. Giao diện quản lý Hadoop

Khi NameNode đang chạy, có thể mở giao diện HDFS tại:

http://localhost:9870

Có thể sử dụng giao diện này để kiểm tra trạng thái HDFS và duyệt hệ thống tệp.

Giao diện ResourceManager tại http://localhost:8088 chỉ khả dụng khi dịch vụ ResourceManager được cấu hình và đang chạy.

## 10. Ghi chú

- Đường dẫn HDFS phân biệt chữ hoa và chữ thường.
- Dự án sử dụng đường dẫn `/BigData/raw/taxi` làm thư mục đầu vào.
- Các tệp cấu hình trong `config/` là bản tham khảo; cần kiểm tra lại các đường dẫn và cấu hình khi triển khai trên môi trường khác.
- Không đưa toàn bộ dữ liệu CSV dung lượng lớn vào GitHub repository.
