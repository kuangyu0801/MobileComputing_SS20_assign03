# MobileComputing_SS20_assign03

# Task01
- 1.1: Write to Database:
- 1.2: Read from Database

## Design
兩個按鍵、read, write, 一個City text input 一個city temperature in/out

# Task02
- 2.1: Continuously display the latest temperature value of a city
- 2.2: Show todays average from one location

## Design
get the full list of cities
using listener callback

## 需掌握知識點
- Firebase使用
- JSON format
- NoSQL data base
- Firebase in a Weekend on Udacity

## 問題
- 要怎麼建立database:進到console裡面->選database(for debugging choose "test")
- 我要怎麼在後台看到Realtime Database的資料:進到console裡面->選database->選data
- 要如何取的root directory

## 實作
- 先用自己的firebase開發，完成之後再改用助教的
- 每個人先在自己的feature branch上面開發, 最後我再幫大家merge到master

## Firebase使用
[Add Firebase to your Android project](https://firebase.google.com/docs/android/setup)
[Video of Adding Firebase to Android Studio](https://www.youtube.com/watch?v=9qe_A3F-_f0)

[Choose a Database: Cloud Firestore or Realtime Database](https://firebase.google.com/docs/database/rtdb-vs-firestore#writes_and_transactions)
[Readtime Database Intro](https://firebase.google.com/products/realtime-database/?authuser=0)

## JSON架構

## How to Use JSON on Firebase
- All Firebase Realtime Database data is stored as JSON objects.
[Structure Your Database](https://firebase.google.com/docs/database/web/structure-data)
- console -> three dor at right -> Import JSON
## NoSQL
[What is NoSQL?](https://www.youtube.com/watch?v=BgQFJ_UNIgw)
NoSQL: semi-structured database: can store first then categrorize later
SQL is a structured database: data need to fit into the predifined structure in order to be stored

# How to create your own git feature branch?
- 建立branch
$ git branch <branchname>
- 查看branch列表
$ git branch
- 想在自己的branch進行提交，要先切換到自己的branch
$ git checkout <yourbranch>
- 如果想要同時建立然後切換到該branch
$ git branch -b <branchname>
- 切換到自己的branch之後就可以開始add, commit, push
