.class public Ex3_2

.super java/lang/Object
.method public <init>()V
.limit stack 10
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method
.method public static main([Ljava/lang/String;)V
.limit stack 10
  ldc "hello"
  ldc " lucas \n"
  invokestatic sal/Library/concat(Ljava.lang.String;Ljava.lang.String;)Ljava.lang.String;
  astore 1
  aload 1
  invokestatic sal/Library/print(Ljava.lang.String;)V
  ldc "2017 "
  invokestatic sal/Library/len(Ljava.lang.String;)I
  ldc 2105
  invokestatic sal/Library/toStr(I)Ljava.lang.String;
  swap
  invokestatic sal/Library/toStr(I)Ljava.lang.String;
  swap
  invokestatic sal/Library/concat(Ljava.lang.String;Ljava.lang.String;)Ljava.lang.String;
  ldc " "
  invokestatic sal/Library/concat(Ljava.lang.String;Ljava.lang.String;)Ljava.lang.String;
  ldc 25
  invokestatic sal/Library/toStr(I)Ljava.lang.String;
  invokestatic sal/Library/concat(Ljava.lang.String;Ljava.lang.String;)Ljava.lang.String;
  ldc "\nlucas "
  invokestatic sal/Library/concat(Ljava.lang.String;Ljava.lang.String;)Ljava.lang.String;
  ldc 5
  ldc 2
  imul
  invokestatic sal/Library/toStr(I)Ljava.lang.String;
  invokestatic sal/Library/concat(Ljava.lang.String;Ljava.lang.String;)Ljava.lang.String;
  ldc "\n"
  invokestatic sal/Library/concat(Ljava.lang.String;Ljava.lang.String;)Ljava.lang.String;
  invokestatic sal/Library/print(Ljava.lang.String;)V
    return
.limit locals 2
.end method

