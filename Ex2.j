.class public Ex2

.super java/lang/Object
.method public <init>()V
.limit stack 10
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method
.method public static main([Ljava/lang/String;)V
.limit stack 10
  ldc 0
  istore 1
  ldc "Enter number (a): "
  invokestatic sal/Library/print(Ljava.lang.String;)V
  invokestatic sal/Library/readInt()I
  istore 2
  ldc "Enter number (b): "
  invokestatic sal/Library/print(Ljava.lang.String;)V
  invokestatic sal/Library/readInt()I
  istore 3
  ldc "Enter number (c): "
  invokestatic sal/Library/print(Ljava.lang.String;)V
  invokestatic sal/Library/readInt()I
  istore 4
  iload 2
  iload 3
  iload 4
  imul
  iadd
  istore 1
  ldc "Result is "
  invokestatic sal/Library/print(Ljava.lang.String;)V
  iload 1
  invokestatic sal/Library/print(I)V
  ldc "\n"
  invokestatic sal/Library/print(Ljava.lang.String;)V
    return
.limit locals 5
.end method

