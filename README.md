# MobileComputing_SS20_assign03

# Task01
- 1.1: Write to Database:
- 1.2: Read from Database: using onSuccess call back or just normal listener

### Design
兩個按鍵、read, write, 一個City text input 一個city temperature in/out

# Task02
- 2.1: Continuously display the latest temperature value of a city: choose a city(do it with drop down or textEdit)
- 2.2: Show todays average from one location

### Design
get the full list of cities
using listener callback

## 需掌握知識點
- Firebase使用
- JSON format
- NoSQL data base
- Firebase in a Weekend on Udacity

## 問題
- Done 要怎麼建立database:進到console裡面->選database(for debugging choose "test")
- Done 我要怎麼在後台看到Realtime Database的資料:進到console裡面->選database->選data
- 要如何取的root directory
- 获取某个节点的值，只能用监听事件吗？
- Double truncate

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
- 建立local branch go to menu bar click "VCS" -> "Git" -> "Branches" -> "create branch"
- 建立remote branch: push 之後就會自動建立 remote branch
- 如何merge changes?  "VCS" -> "Git" -> "Merge Changes" 詳情參考
[JetBrain文章](https://www.jetbrains.com/help/idea/apply-changes-from-one-branch-to-another.html)
[Youtube教學How and when do I merge or rebase?](https://youtu.be/Nftif2ynvdA)

