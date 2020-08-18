class member {
  String username;
  int id;

  member({
    this.username,
    this.id
  });
  factory member.fromJson(Map<String, dynamic> json) {
    return new member(
      username: json['userName'],
      id: json['id'],
    );
  }
}