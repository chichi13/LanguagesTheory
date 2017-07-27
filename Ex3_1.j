.class public Ex3_1

.super java/lang/Object
.method public <init>()V
.limit stack 10
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method
.method public static main([Ljava/lang/String;)V
.limit stack 10
  ldc 3
  invokestatic sal/Library/toStr(I)Ljava.lang.String;
  astore 1
  ldc "85"
  invokestatic sal/Library/toInt(Ljava.lang.String;)I
  istore 2
  ldc "hey"
  invokestatic sal/Library/len(Ljava.lang.String;)I
  istore 3
  aload 1
  invokestatic sal/Library/print(Ljava.lang.String;)V
  ldc "\n"
  invokestatic sal/Library/print(Ljava.lang.String;)V
  iload 2
  invokestatic sal/Library/print(I)V
  ldc "\n"
  invokestatic sal/Library/print(Ljava.lang.String;)V
  iload 3
  invokestatic sal/Library/print(I)V
  ldc "\n"
  invokestatic sal/Library/print(Ljava.lang.String;)V
    return
.limit locals 4
.end method

