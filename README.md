# 모바일프로그래밍 7조
2021-2 모바일프로그래밍 팀프로젝트

## 팀원 소개
```
박세열
STUDENT NUMBER : 
```

```
강경민
STUDENT NUMBER : 
```

```
황재일
STUDENT NUMBER : 20203169
```

```
최은솔
STUDENT NUMBER : 
```

```
전성재
STUDENT NUMBER : 
```

## 프로젝트 소개
![image](https://user-images.githubusercontent.com/66048830/143769037-e1edcc30-697e-4495-8016-721700e1a9c9.png)\
강의별로 존재하는 일정들을 파악하기 쉽도록 진행한 프로젝트\
국민대학교 ecampus에 존재하는 달력 기능은, 강의들이 수강 마감 날짜가 아닌 수강 가능 기간에 모두 표시되어있어 매우 난잡함.\
특히 모바일에서는 일정 파악 자체가 쉽지 않아 각 강의를 일일이 확인하며 정확한 일정을 파악하여야 함.\
\
때문에, 언제 어디서나 각 강의의 일정 마감일을 정확하게 파악하고 관리할 수 있도록 하기 위해서 해당 프로젝트를 진행함.

<hr>

## 1. 개발 환경

* 운영체제 : Windows
* 개발 도구 : Android Studio 2020.3.1
* 개발 언어 : Java
* 데이터베이스 : Firebase
* AVD SDK : Pixel 2 API 30 (Android 11.0)

## 2. 기능 요약 설명
### 교수의 경우
* 강의 채널 생성 가능
* 강의 채널마다 학생들에게 공유되는 일정 추가 가능
### 학생의 경우
* 강의 검색 후 리스트에 등록 가능
### 공통
* 회원가입/로그인을 통한 계정 정보 저장 및 관리
* 강의 리스트에 강의 채널 등록 및 관리 가능
* 강의 채널별로 각 각의의 일정 마감일 확인 가능
* '모든 일정' 채널을 통해 등록한 모든 강의의 일정 통합 확인 가능

## 3. 데이터베이스 구조
### UserInfo
![image](https://user-images.githubusercontent.com/66048830/143769466-af007a97-6467-4a88-b524-ad4eeb9eb424.png)\
회원가입한 유저의 정보를 저장해두는 데이터베이스
* __Address__\
유저의 거주 주소
* __Authority__\
유저가 교수인지 학생인지를 나타내는 값.\
이 값에 따라 어플의 기능이 나누어진다.
* __Email__\
유저의 이메일 주소
* __Id__\
회원가입시 생성된 유저의 UID
* __Name__\
유저의 이름
* __Password__\
유저의 비밀번호
* __PhoneNumber__\
유저의 전화번호
* __StuNum__\
유저의 학번

<hr>

### LectureInfo
![image](https://user-images.githubusercontent.com/66048830/143769613-28ba458f-a6dd-4c37-88bc-74883c4ac89c.png)\
강의에 대한 정보를 저장해두는 데이터베이스\
N개의 강의가 0 ~ N-1까지의 UID로 저장된다.
* __Day__\
강의가 진행되는 요일
* __Division__\
강의의 분반
* __Name__\
강의의 이름
* __Professor__\
강의를 진행하는 교수님의 성함
* __Time__\
강의가 진행되는 시간

<hr>

### ClassInfo
![image](https://user-images.githubusercontent.com/66048830/143769696-9bfeeb79-b446-41ea-a171-4369f304e763.png)\
강의를 수강하는 학생들에 대한 정보를 저장해두는 데이터베이스\
<__"학번" : "이름"__>의 구조로 저장되어진다.

<hr>

### UserLectureInfo
![image](https://user-images.githubusercontent.com/66048830/143769785-d63a9dc3-d22c-4564-ad6b-f42e17ddafa4.png)\
유저별로 리스트에 등록해둔 강의들에 대한 정보를 저장해두는 데이터베이스\
<__"유저 UID" : "강의 UID"__>의 구조로 저장되어진다.

<hr>

### TodoInfo
![image](https://user-images.githubusercontent.com/66048830/143769878-b894f031-037f-4203-b44d-00160c9262af.png)\
강의별로 강의 채널에 등록된 강의의 일정에 대한 정보를 저장해두는 데이터베이스\
"강의 UID" → "일정의 마감 날짜" → "일정 데이터"의 구조로 저장되어진다.\
"일정 데이터"는 다음과 같은 구조로 저장되어진다.

* __Date__\
일정의 마감 날짜
* __RegisterNum__\
일정의 번호. 일정 데이터의 Key값과 같은 값을 가진다.
* __Title__\
일정의 자세한 내용
* __Type__\
일정의 간단한 분류.\
과제, 퀴즈, 강의, 시험 4가지로 분류된다.

<hr>

## 4. 기능 구현

### 박세열

### 강경민

### 황재일

### 최은솔

### 전성재
