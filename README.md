# HongJiSeopPost(프로젝트명 : webpost)

## 목차
- [히스토리](#히스토리)
- [구현 기능](#구현-기능)
- [의존성(기술 스택)](#의존성기술-스택)
- [패키지 구조](#패키지-구조)
- [API](#api)
- [ERD](#erd)
## 히스토리
* Spring Security 적용
  * JWT Token적용
  * Refresh Token, Access Token 적용
* Exception
* Test
  * Mockito 적용
* 

## 구현 기능
- **게시판 기능**
    - 모든 게시글 및 특정 게시글 조회
    - 게시글 검색 (제목, 내용, 작성자)
    - 게시글 작성 [회원]
    - 게시글 수정 [회원, 게시글 작성자]
    - 게시글 삭제 [회원, 게시글 작성자]
    - 게시글 답글 작성 [회원]
    - 게시글 좋아요 추가/삭제

- **댓글 기능**
    - 댓글 조회
    - 댓글 작성 [회원]
    - 댓글 수정 [회원, 댓글 작성자]
    - 댓글 삭제 [회원, 댓글 작성자]

- **회원 기능**
    - 회원가입
    - 로그인/로그아웃

## 의존성
|       의존성       |
|:---------------:|
|   Spring Boot   | 
|   Spring Web    |
| Spring Data Jpa |
| Bean Validation |
|     Lombok      |
|  MySQL Driver   |
|    Security     |
|     Mockito     |
## 패키지 구조

## API


## ERD
![img.png](img.png)