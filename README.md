# Rsupport

## 사용 기술

- JDK 11
- Spring Boot 2.6.3
- Data-JPA (Hibernate) + QueryDSL
- H2 
- Junit5, AssertJ, Reassured

## 프로젝트 요구사항

- [x] 공지사항
    - [x] 등록
        - [x] 등록내용 : `제목, 내용, 공지 시작일시, 공지 종료일시, 첨부파일(여러개)`
        - [x] 내용이 충분히 전달되지 않아 실패시
          - [x] 제목이 전달되지 않았을 경우 : <br>`공지사항 제목이 반드시 전달되어야 합니다, 인터널에러코드-3, 400 Bad Request`
          - [x] 제목이 비어있을 경우 : <br>`공지사항 제목이 비어있어서는 안됩니다. 인터널에러코드-4, 400 Bad Request`
          - [x] 내용이 전달되지 않았을 경우 : <br>`공지사항 내용이 반드시 전달되어야 합니다, 인터널에러코드-5, 400 Bad Request`
          - [x] 내용이 비어있을 경우 : <br>`공지사항 내용이 비어있어서는 안됩니다. 인터널에러코드-6, 400 Bad Request`
        - [x] 성공시 응답내용 : <br>`공지사항 PK 번호, 공지사항 등록이 성공적으로 완료되었습니다, 200 OK`
    - [x] 수정
        - [x] 존재하지 않는 공지를 수정 시도하여 실패시 : <br>`해당 공지사항은 존재하지 않습니다, 인터널에러코드-6, 404 Not Found`
        - [x] 내용이 충분히 전달되지 않아 실패시
          - [x] 공지사항 번호가 전달되지 않았을 경우 : <br>`공지사항 번호가 반드시 전달되어야 합니다, 인터널 에러코드-2, 400 Bad Request`
          - [x] 제목이 전달되지 않았을 경우 : <br>`공지사항 제목이 반드시 전달되어야 합니다, 인터널에러코드-3, 400 Bad Request`
          - [x] 제목이 비어있을 경우 : <br>`공지사항 제목이 비어있어서는 안됩니다, 인터널에러코드-4, 400 Bad Request`
          - [x] 내용이 전달되지 않았을 경우 : <br>`공지사항 내용이 반드시 전달되어야 합니다, 인터널에러코드-5, 400 Bad Request`
          - [x] 내용이 비어있을 경우 : <br>`공지사항 내용이 비어있어서는 안됩니다, 인터널에러코드-6, 400 Bad Request`
          - [x] 성공시 응답내용 : <br>`공지사항 PK 번호, 공지사항 수정이 성공적으로 완료되었습니다, 200 OK`
    - [x] 삭제
        - [x] 존재하지 않는 공지를 삭제 시도하여 실패시 : <br>`해당 공지사항은 존재하지 않습니다, 인터널에러코드-7, 404 Not Found`
        - [x] 성공시 응답내용 : <br>`공지사항 삭제가 성공적으로 완료되었습니다, 200 OK`
    - [x] 조회
        - [x] 성공시 응답내용 : <br>`제목, 내용, 등록일시, 조회수, 작성자`
        - [x] 존재하지 않는 공지를 조회 시도하여 실패시 : <br>`해당 공지사항은 존재하지 않습니다, 인터널에러코드-7, 404 Not Found`

## 공지사항

---

- 등록 `POST` /notice NoticeController#registerNotice(@RequestBody NoticeRegisterDTO)
- 수정 `POST` /notice NoticeController#updateNotice(@RequestBody NoticeUpdateDTO)
- 삭제 `DELETE` /notice/{noticeId} NoticeController#removeNotice(@PathVariable Long noticeId)
- 조회 `GET` /notice/{noticeId} NoticeController#getNotice(@PathVariable Long noticeId)

## 사용 안내

---

### 프로젝트 다운로드, 빌드 및 실행

```shell
git clone https://github.com/chaenabi/rsupport
또는 깃허브에서 프로젝트를 zip 파일로 다운로드 및 압축해제
```

윈도우일 경우 cmd 창에서 아래의 명령어를 순차적으로 실행하세요.

```shell
cd rsupport
gradlew.bat bootJar 
java -jar ./build/libs/rsupport-0.0.1.jar
```

리눅스 / 맥일 경우 terminal 에서 아래의 명렁어를 순차적으로 실행하세요.

```shell
cd rsupport
source gradlew bootJar 
java -jar ./build/libs/rsupport-0.0.1.jar
```

## API 사용법

--- 반드시 위 작업을 먼저 선행한 후에 진행하세요.

### 공지사항 등록
```shell
curl -X POST http://localhost:8080/v1/notice -H "Content-Type: multipart/form-data" -F "data={\"title\":\"saved notice title\", \"content\":\"saved notice content\"};type=application/json"
```

### 공지사항 등록 (다중 첨부파일 포함 요청)
```shell
curl -X POST http://localhost:8080/v1/notice -H "Content-Type: multipart/form-data" -F "data={\"title\":\"saved notice title\", \"content\":\"saved notice content\"};type=application/json" -F 'attachFiles[]=@./알서포트과제.txt;type=multipart/form-data' -F 'attachFiles[]=@./coverage_20220129.png;type=multipart/form-data'
```
`*주의사항*` 실재하는 파일을 전송해야 합니다.

예를 들어 첨부하려는 파일명이 그림.png이고 현재 디렉토리에 존재한다면,

반드시 아래와 같은 방식으로 위의 명령어 중 일부를 수정한 뒤 요청을 보내셔야 합니다.
```
'attachFiles[]=@./알서포트과제.txt;multipart/form-data'

부분을

'attachFiles[]=@./그림.png;type=multipart/form-data'
 
다음과 같은 방식으로 변경하여 요청합니다.
```

### 공지사항 수정
```shell
curl -X POST http://localhost:8080/v1/notice-update -H "Content-Type: multipart/form-data" -F "data={\"noticeId\":1, \"title\":\"modified title\", \"content\":\"saved content\"};type=application/json"
```

### 공지사항 단건 조회
```shell
curl -X GET http://localhost:8080/v1/notice/1 -H "Content-Type: application/json"
```

### 공지사항 삭제
```shell
curl -X DELETE http://localhost:8080/v1/notice/1 -H "Content-Type: application/json"
```

