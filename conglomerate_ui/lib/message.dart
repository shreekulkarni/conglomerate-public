class message {
  String content;
  int id;
  String sender;
  String timeStamp;
  bool liked;
  int read;
  List userRead;

  message({this.content, this.id, this.sender, this.timeStamp, this.read, this.userRead, this.liked});

  factory message.fromJson(Map<String, dynamic> json) {
    var readBy = json['read'];
    var readByList = readBy as List;
    return new message(
        content: json['content'],
        id: json['id'],
        liked: json['liked'],
        sender: json['senderName'],
        timeStamp: json['timestamp'],
        read: readByList.length,
        userRead: readByList,
    );
  }
}