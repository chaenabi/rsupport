# Rsupport

## 사용 기술

- JDK 11
- Spring Boot 2.6.3
- Data-JPA (Hibernate) + QueryDSL
- H2 
- Junit5, AssertJ, Reassured

## 프로젝트 요구사항

- [ ] 공지사항
    - [ ] 등록: ADMIN 권한이 있어야 등록 가능
        - [ ] 등록내용 : `제목, 내용, 공지 시작일시, 공지 종료일시, 첨부파일(여러개)`
        - [ ] 권한 부족으로 인한 실패시 : `공지사항 작성 권한이 없습니다, 인터널에러코드-1, 401 UnAuthorized`
        - [ ] 내용이 충분히 전달되지 않아 실패시
          - [ ] 제목이 전달되지 않았을 경우 : `공지사항 제목이 반드시 전달되어야 합니다, 인터널에러코드-2, 400 Bad Request`
          - [ ] 제목이 비어있을 경우 : `공지사항 제목이 비어있어서는 안됩니다. 인터널에러코드-3, 400 Bad Request`
          - [ ] 내용이 전달되지 않았을 경우 : `공지사항 내용이 반드시 전달되어야 합니다, 인터널에러코드-4, 400 Bad Request`
          - [ ] 내용이 비어있을 경우 : `공지사항 내용이 비어있어서는 안됩니다. 인터널에러코드-5, 400 Bad Request`
        - [ ] 성공시 응답내용 : `공지사항 PK 번호, 공지사항 등록이 성공적으로 완료되었습니다, 200 OK`
    - [ ] 수정
        - [ ] 존재하지 않는 공지를 수정 시도하여 실패시 : `해당 공지사항은 존재하지 않습니다, 인터널에러코드-6, 404 Not Found`
        - [ ] 내용이 충분히 전달되지 않아 실패시
          - [ ] 제목이 전달되지 않았을 경우 : `공지사항 제목이 반드시 전달되어야 합니다, 인터널에러코드-2, 400 Bad Request`
          - [ ] 제목이 비어있을 경우 : `공지사항 제목이 비어있어서는 안됩니다, 인터널에러코드-3, 400 Bad Request`
          - [ ] 내용이 전달되지 않았을 경우 : `공지사항 내용이 반드시 전달되어야 합니다, 인터널에러코드-4, 400 Bad Request`
          - [ ] 내용이 비어있을 경우 : `공지사항 내용이 비어있어서는 안됩니다, 인터널에러코드-5, 400 Bad Request`
          - [ ] 성공시 응답내용 : `공지사항 PK 번호, 공지사항 수정이 성공적으로 완료되었습니다, 200 OK`
    - [ ] 삭제
        - [ ] 존재하지 않는 공지를 삭제 시도하여 실패시 : `해당 공지사항은 존재하지 않습니다, 인터널에러코드-6, 404 Not Found`
        - [ ] 성공시 응답내용 : `공지사항 삭제가 성공적으로 완료되었습니다, 200 OK`
    - [ ] 조회
        - [ ] 성공시 응답내용 : `제목, 내용, 등록일시, 조회수, 작성자`
        - [ ] 존재하지 않는 공지를 조회 시도하여 실패시 : `해당 공지사항은 존재하지 않습니다, 인터널에러코드-6, 404 Not Found`

## 공지사항

- 등록 `POST` /notice NoticeController#registerNotice(@RequestBody NoticeRegisterDTO)
- 수정 `PATCH` /notice NoticeController#updateNotice(@RequestBody NoticeUpdateDTO)
- 삭제 `DELETE` /notice/{noticeId} NoticeController#removeNotice(@PathVariable Long noticeId)
- 조회 `GET` /notice/{noticeId} NoticeController#getNotice(@PathVariable Long noticeId)

# 사용 안내
```shell
$ ./gradlew bootJar
$ java -jar ./build/libs/rsupport[-0.0.1].jar
```
```shell
$ curl -X GET http://localhost:8080/notice/1 -H "Content-Type: application/json"
```