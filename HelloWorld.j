.class public HelloWorld

.super java/lang/Object
.method public <init>()V
.limit stack 10
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method
.method public static main([Ljava/lang/String;)V
.limit stack 10
  ldc "Hello World!\n"
  invokestatic sal/Library/print(Ljava.lang.String;)V
    return
.limit locals 1
.end method

