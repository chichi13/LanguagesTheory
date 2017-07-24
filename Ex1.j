.class public Ex1

.super java/lang/Object
.method public <init>()V
.limit stack 10
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method
.method public static main([Ljava/lang/String;)V
.limit stack 10
  ldc "Enter a : \n"
  invokestatic sal/Library/print(Ljava.lang.String;)V
  invokestatic sal/Library/readInt()I
  istore 1
  ldc "Enter b : \n"
  invokestatic sal/Library/print(Ljava.lang.String;)V
  invokestatic sal/Library/readInt()I
  istore 2
  ldc "Enter c : \n"
  invokestatic sal/Library/print(Ljava.lang.String;)V
  invokestatic sal/Library/readInt()I
  istore 3
  ldc 0
  istore 4
  iload 1
  iload 2
  iadd
  iload 3
  iadd
  istore 4
  ldc "result = "
  invokestatic sal/Library/print(Ljava.lang.String;)V
  iload 4
  invokestatic sal/Library/print(I)V
  ldc "\n"
  invokestatic sal/Library/print(Ljava.lang.String;)V
    return
.limit locals 5
.end method

