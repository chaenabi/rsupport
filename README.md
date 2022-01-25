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
        - [ ] 내용이 충분히 전달되지 않아 실패시 : `공지사항을 등록하기 위한 정보가 충분히 작성되지 않았습니다, 인터널에러코드-999, 400 Bad Request`
          - [ ] 제목이 비어있을 경우 : `제목이 비어있어서는 안됩니다. 인터널에러코드-2, 400 Bad Request`
          - [ ] 내용이 비어있을 경우 : `내용이 비어있어서는 안됩니다. 인터널에러코드-3, 400 Bad Request`
    - [ ] 수정
        - [ ] 존재하지 않는 공지를 수정 시도하여 실패시 : `수정하려는 공지사항이 더이상 존재하지 않습니다, 인터널에러코드-4, 404 Not Found`
        - [ ] 내용이 충분히 전달되지 않아 실패시 : `공지사항을 등록하기 위한 정보가 충분히 작성되지 않았습니다, 인터널에러코드-999, 400 Bad Request`
          - [ ] 제목이 비어있을 경우 : `제목이 비어있어서는 안됩니다, 인터널에러코드-5, 400 Bad Request`
          - [ ] 내용이 비어있을 경우 : `내용이 비어있어서는 안됩니다, 인터널에러코드-6, 400 Bad Request`
    - [ ] 삭제
        - [ ]  존재하지 않는 공지를 삭제 시도하여 실패시 : `삭제하려는 공지사항이 이미 존재하지 않습니다, 인터널에러코드-7, 404 Not Found`
    - [ ] 조회
        - [ ] 응답내용 : `제목, 내용, 등록일시, 조회수, 작성자` 
        - [ ] 존재하지 않는 공지를 조회 시도하여 실패시 : `해당 공지사항은 존재하지 않습니다, 인터널에러코드-8, 404 Not Found`

## 공지사항

- 등록 `POST` /notice NoticeController#registerNotice(@RequestBody NoticeRegisterDTO)
- 수정 `PATCH` /notice NoticeController#updateNotice(@RequestBody NoticeUpdateDTO)
- 삭제 `DELETE` /notice/{noticeId} NoticeController#removeNotice(@PathVariable Long noticeId)
- 조회 `GET` /notice/{noticeId} NoticeController#getNotice(@PathVariable Long noticeId)

# 사용 안내
```shell
$ ./gradlew bootJar
$ java -jar [rsupport.jar]
```
```shell
$ curl -X GET http://localhost:8080/notice/1 -H "Content-Type: application/json"
```