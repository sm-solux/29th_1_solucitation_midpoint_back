# 프로젝트 소개
MIDPOINT : 우리 어디서 만날까?

중간값 알고리즘을 사용한
사용자의 목적에 맞는 중간 지점 찾기 서비스 

## 1. 프로젝트 동기 
### 만남 장소에서 발생할 수 있는 문제점
- 여러 사람이 만날 때 모임 장소를 정하는 데 어려움이 있음
- 모두가 아는 장소로 와야 하는 문제
- 시간과 비용이 비효율적으로 소모됨
- 중간 지점을 찾아도 목적에 맞는 장소를 재검색해야 함

### 이에 대한 해결책
- 중간 지점에서 만남
   - 모두에게 시간적으로 유리
   - 공평한 장소 선정 가능
- 만남 목적 반영
   - 만남의 목적에 맞는 장소 제공
   - 장소 선택에 대한 번거로움 감소
   - 특정 활동에 적합한 장소 선정으로 모임의 질 향상

## 2. 팀원 소개 및 역할분담
<img width="485" alt="image" src="https://github.com/user-attachments/assets/ca193b43-625b-4c80-a44e-1779969fa039">
<img width="485" alt="image" src="https://github.com/user-attachments/assets/cb137720-120c-4f4d-9aa3-98212269ca31">
<img width="485" alt="image" src="https://github.com/user-attachments/assets/03dbcc5c-b67c-4ae2-ae69-55eaf3a597be">

## 3. 프로젝트 아키텍처
<img width="485" alt="image" src="https://github.com/user-attachments/assets/f94ac091-0dfd-4697-b745-c39d0df5c450">

## 4. 메뉴 구조 
<img width="485" alt="image" src="https://github.com/user-attachments/assets/6f08a7ca-d15c-44c0-a06e-4da7d7d2202a">

현 프로젝트는 크게 로그인 페이지, 홈 화면 페이지, 게시판 페이지, 마이페이지로 구성되어 있습니다. 

웹 페이지의 상단의 네비게이션 바를 통해 해당 페이지로 이동하실 수 있습니다. 

## 5. 구현 기능 소개 
<img width="485" alt="image" src="https://github.com/user-attachments/assets/e2924981-15f6-46ef-92dd-b9ee6d46c7f0">
<img width="485" alt="image" src="https://github.com/user-attachments/assets/856b0381-212c-4f98-ac33-9bd10afa6682">

## 6. 페이지 캡쳐
### 1. 로그인 페이지 
![스크린샷 2024-08-04 023654](https://github.com/user-attachments/assets/a1f71d48-0d95-4d74-9b97-2f738f593c79)

### 1.1 카카오 로그인 페이지
![스크린샷 2024-08-04 023721](https://github.com/user-attachments/assets/9579b6fb-b4dc-44f8-8ba2-9fccbb07d460)

### 1.2 자체 로그인 페이지 
사용자는 '아이디' 칸에 아이디 또는 이메일로 입력합니다. 
비밀번호가 일치하지 않으면 '아이디 또는 비밀번호가 일치하지 않습니다' 에러 문구가 뜹니다.
![스크린샷 2024-08-04 025547](https://github.com/user-attachments/assets/0f255be6-da5f-4d21-aade-e27637b04a02)


### 2. 홈 페이지
- 로그인을 하지 않은 상태라면 즐겨찾기 장소나 즐겨찾기 친구를 클릭해도 '로그인 후 이용해주세요' 라는 문구가 뜹니다.
- 아래 '검색 기록'에 현재 페이지에서 이전에 사용자가 검색한 장소 리스트들이 뜹니다.
![image](https://github.com/user-attachments/assets/56d7c79d-3002-4b58-bd5f-9c614d3523ae)
![image](https://github.com/user-attachments/assets/c9ba9621-3cfa-4d65-bb35-9a1f204e85a8)

- 주소와 목적을 적절히 선택합니다. 선택한 장소는 상단부터 화면에 보여지며, 오른쪽 '삭제' 버튼을 통해 삭제가 가능합니다.
- 주소를 선택시 등록한 친구의 주소를 선택할 수 있으며, 등록한 즐겨찾기 장소 중 하나를 선택할 수 있습니다.
- 등록한 즐겨찾기 장소는 집과 직장/학교입니다.
![image](https://github.com/user-attachments/assets/aa566de2-f40b-4113-8f6c-6e5bf8ad8476)


### 3. 장소 페이지
- 다음과 같이 목적에 맞는 장소들 리스트가 왼쪽에 나오고, 오른쪽에는 중간지점 위치를 마커로 찍은 map이 나옵니다.
![스크린샷 2024-08-04 025836](https://github.com/user-attachments/assets/bd8736b3-6f12-49ab-84e0-c1b3b08f5c96)

- 해당 장소를 클릭하면 리뷰 페이지로 넘어갑니다. 
![스크린샷 2024-08-04 025847](https://github.com/user-attachments/assets/33170ca1-c168-4926-ba62-cbe526ef6ff2)

- 장소 저장이나 공유를 하고 싶은 경우 '장소 선택'을 누른 뒤 '공유' 또는 '장소 저장' 버튼을 클릭합니다.
  - 공유
![image](https://github.com/user-attachments/assets/f3b7c4d4-be53-4090-b300-e2db144aa459)

  - 장소 저장
    - 장소 저장한 장소는 마이페이지 > 검색기록에서 확인할 수 있습니다.
![스크린샷 2024-08-04 030028](https://github.com/user-attachments/assets/84d83d9c-7b9c-4485-aca2-3be90e7258b8)

### 4. 커뮤니티 게시판 
- 게시판 등록, 수정, 삭제, 리스트 조회, 게시글 상세 보기가 가능합니다.
- 게시판 리스트 
![스크린샷 2024-08-04 030753](https://github.com/user-attachments/assets/396a7459-64be-424e-b83d-3125edab3a29)

- 게시글 상세보기
![스크린샷 2024-08-04 030811](https://github.com/user-attachments/assets/f0439d67-5eb2-484c-bb6a-d943c28e595f)

- 게시판 등록
![스크린샷 2024-08-04 031135](https://github.com/user-attachments/assets/fa814cf1-5fac-4400-9549-339e92e44185)

- 게시글 수정
![스크린샷 2024-08-04 031415](https://github.com/user-attachments/assets/6b9d41ee-1ac1-4adb-a0b0-a63cb1b230f6)

- 게시글 해시태그로 보기
![스크린샷 2024-08-04 031645](https://github.com/user-attachments/assets/f7b2127d-f921-437a-a854-0104abfce2ad)

- '공'으로 키워드 검색하기 
![스크린샷 2024-08-04 032142](https://github.com/user-attachments/assets/c9aef050-538c-4ad2-aa75-7fe5618e520c)

### 5. 목적 테스트 질문 리스트
- 사용자가 어떠한 목적을 선택할지 모를 때를 대비하여 목적 테스트를 만들었습니다. 
![스크린샷 2024-08-04 031826](https://github.com/user-attachments/assets/20c2307b-2a0b-43ee-8bff-91b28ebcebcc)
![스크린샷 2024-08-04 031830](https://github.com/user-attachments/assets/89735c26-38f3-4d9b-b778-f975dde9ea9f)
![스크린샷 2024-08-04 031929](https://github.com/user-attachments/assets/5e003d74-ebf1-4551-b0ab-29804cdfd7c5)
![스크린샷 2024-08-04 031841](https://github.com/user-attachments/assets/82aa3c71-1d46-4d72-b894-b06f9050c321)

### 6. 즐겨찾기-장소, 즐겨찾기-친구
- 즐겨찾기 장소 추가, 편집, 삭제, 리스트 보기, 상세 보기(친구 이름, 주소)가 가능합니다.
- 잘겨찾기 친구 추가, 편집, 삭제, 리스트 보기, 상세 보기(장소 이름,주소)가 가능합니다.
  
- 즐겨찾기 장소, 친구 전체 리스트 보기 
![스크린샷 2024-08-04 030355](https://github.com/user-attachments/assets/3381b7e3-2b8a-4fa8-9132-6aa84b558e46)

- 즐겨찾기 장소 > 집 > 등록
![스크린샷 2024-08-04 030052](https://github.com/user-attachments/assets/3af89ce2-65d1-4ceb-8873-ebce9d048d68)

- 즐겨찾기 장소 > 집 > 상세보기
![스크린샷 2024-08-04 030058](https://github.com/user-attachments/assets/7e1fd8d0-6e0c-487b-9f06-a3adf3a219a6)

- 즐겨찾기 장소 > 집 > 편집하기
![스크린샷 2024-08-04 030111](https://github.com/user-attachments/assets/215d8003-1f29-4acf-b719-b624c7260984)

- 즐겨찾기 장소 > 직장/학교 > 삭제하기
- 마찬가지로 등록, 상세보기, 편집하기도 모두 가능하지만, 설명에는 생략했습니다.

![스크린샷 2024-08-04 030154](https://github.com/user-attachments/assets/84e95de7-d6cc-4199-88ab-525500c11a43)
![스크린샷 2024-08-04 030158](https://github.com/user-attachments/assets/a6bc0311-f5d1-440e-9a57-06675cd75ba4)


- 즐겨찾기 친구 > 등록
![스크린샷 2024-08-04 030229](https://github.com/user-attachments/assets/935aa845-df62-4fb6-ba60-e7a5647b39f7)

- 즐겨찾기 친구 상세보기
![스크린샷 2024-08-04 030234](https://github.com/user-attachments/assets/f5cea657-7d28-43e3-a3f7-e92a1b8703bb)

- 마찬가지로 즐겨찾기 수정, 삭제가 모두 가능하지만, 생략하도록 하겠습니다. 

## 6. 관련 주소
- <a href="https://www.figma.com/design/QzDsktNnMnGtNBkqlKDQYj/%EC%86%94%EB%A3%A9%EC%85%98?node-id=3824-2402&t=O6WUCFepPLPCAHbg-1">Figma Address</a>
- <a href="https://github.com/Solucitation/midpoint-backend">BE Address</a>
- <a href="https://github.com/Solucitation/midpoint-frontend">FE Address</a>

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

# Backend

## ✨Main 기능
- 회원가입/로그인
- 목적에 맞는 중간지점 찾기
- 커뮤니티 게시판
- 즐겨찾기 장소/친구

## 👩‍💻 역할 분담
|     이름         | 프로필                                                              |                                      역할분담                         |
| ---------------------------------- | ------------------------------------------------------------------- | --------------------------------------------------------------------- |
| 노경희 | <img src="https://github.com/user-attachments/assets/f00d127b-695e-48e0-9f94-c21a87582dcc" width="120"/> | api 명세서, 초기 세팅, 회원가입, 이메일 인증, (카카오 로그인), 로그아웃, 회원정보 조회 및 수정, 회원 탈퇴, 백엔드 배포 |
| 문서현 | <img src="https://github.com/user-attachments/assets/494b790b-7d8f-4aff-b86a-8f641f08cb29" width="120"/> | 목적에 맞는 중간지점 찾기, 중간 지점 주변 장소 필터링하기, 특정 장소에 대한 구글 리뷰 끌어오기, 즐겨찾기 장소/친구 등록, 수정, 삭제, 상세보기, 즐겨찾기 리스트 보기 |
| 최연재 | <img src="https://github.com/user-attachments/assets/3edd11dd-ef2d-46c0-9c5f-5ade1b3b7d75" width="120"/> | ERD, 커뮤니티 게시판 (리뷰 게시판)의 글 등록, 수정, 삭제, 상세보기, 좋아요, 해시태그 및 키워드 검색, 장소 검색 기록 저장 및 조회 |


## 🌳 프로젝트 구조
```
├─java
│  └─com
│      └─solucitation
│          └─midpoint_backend
│              ├─domain
│              │  ├─community_board
│              │  │  ├─api
│              │  │  ├─dto
│              │  │  ├─entity
│              │  │  ├─repository
│              │  │  └─service
│              │  ├─email
│              │  │  ├─api
│              │  │  ├─dto
│              │  │  └─service
│              │  ├─FavFriend
│              │  │  ├─api
│              │  │  ├─dto
│              │  │  ├─entity
│              │  │  ├─repository
│              │  │  └─service
│              │  ├─FavPlace
│              │  │  ├─api
│              │  │  ├─dto
│              │  │  ├─entity
│              │  │  ├─repository
│              │  │  ├─service
│              │  │  └─validation
│              │  ├─file
│              │  │  ├─controller
│              │  │  └─service
│              │  ├─history2
│              │  │  ├─api
│              │  │  ├─dto
│              │  │  ├─entity
│              │  │  ├─repository
│              │  │  └─service
│              │  ├─logic
│              │  ├─member
│              │  │  ├─api
│              │  │  ├─dto
│              │  │  ├─entity
│              │  │  ├─exception
│              │  │  ├─repository
│              │  │  └─service
│              │  ├─places
│              │  └─reviews
│              └─global
│                  ├─api
│                  ├─auth
│                  ├─config
│                  └─exception
└─resources
    ├─templates
    └─application.properties
```
