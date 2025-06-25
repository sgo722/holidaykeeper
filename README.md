# 구현 기능 명세
1. 초기 데이터 적재
2. 검색 기능
   1. 연도별 / 국가별 필터 조회
   2. from - to 기간, 공휴일 등 추가 필터 확장 가능성
   3. 페이징 형태로 응답
4. 재동기화
   1. 특정 연도/국가 데이터를 재호출하여 Upsert 가능
5. (선택)배치 자동화
   1. 매년 1월 2일 01:00 KST에 전년도, 금년도 데이터 자동 동기화

---
##  작업 진행 순서
1. 요구 사항 분석
2. 외부 API 분석 
3. 데이터베이스 설계
4. 코드 작성 (테스트 코드 - 구현)
5. 코드 리팩토링
6. 문서화(Swagger)

### 요구 사항 분석
1. 공휴일 데이터 최초 저장 기능 
   - 최초 실행 할 때 최근 5년 x N개 국가를 일괄 적재해야한다.
     1. 최초 실행 시 외부 API에 실행 할 때마다 데이터 적재
     2. 5xN번 만큼 실행한 후 h2에 한번에 적재
2. 공휴일 데이터 조회 기능
   1. 필터 기반 조건
      1. 연도
      2. 국가
      3. 기간
      4. 공휴일
3. 공휴일 데이터 upsert기능
   - 특정 연도,국가 데이터를 재호출하여 upsert 가능
     1. DB 조회 후 있으면 반환
     2. DB 조회 후 없으면 외부 API 실행 후 저장

4. 공휴일 삭제 기능
   1. 특정 연도 공휴일 전체 삭제
   2. 특정 국가 공휴일 전체 삭제

5. 데이터 배치 자동화
   1. 매년 1월 2일 01:00 KST에 전년도,금년도 데이터를 자동 동기화
 

### 외부 API 분석
1. 국가목록 반환 데이터
   1. countryCode : 국가 코드(String)
   2. name : 국가명(String)

2. 특정 연도 공휴일 반환 데이터
   1. date : 날짜(String , YYYY-MM-DD)
   2. localName : 현지이름(String)
   3. name : 영문이름(String)
   4. fixed : 날짜가 고정인지(Boolean)
   5. global(Boolean)
       - true : 전국단위
       - false : 지역단위
   6. countries(Nullable, String List)
      - global이 false인 경우 지역을 표기
   7. launchYear : 도입된 해(Nullable, Integer)
   8. types(Enum List)
      - Public : 법정 공휴일
      - Bank : 은행 휴일
      - School : 학교에서만 쉬는 날
      - Authorities : 공공기간만 쉬는 날
      - Optional : 선택적 휴일
      - Observance : 기념일이지만 법적 공휴일 아님


## 리팩토링 대상 정리
[x] restClient 에러 핸들링  
[x] Country / Holiday 패키지 분리하기  
[x] 전역 에러 핸들링 구현하기
[x] Controller Request 데이터 검증하기

## 할 일 정리
[ ] 처음 데이터 배치처리 + 서비스로 책임분리
[ ] holidayservice 메서드 분리
[ ] scheduler 매일 새벽 3시
[ ] 깃헙 ci 먼들기
[ ] 문서화 후 제출