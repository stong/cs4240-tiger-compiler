# CS4240 GangweedGanggang ***RETARGETABLE*** tiger compiler that does not use LLIR

The coolest part of this compiler is that since I based the design off LLVM basically all of the heavy algorithms (frontend, regalloc, isel, lowering, etc.) are architecture independent.

- [Frontend AST to IR](https://github.com/rollsafe/cs4240-tiger-compiler/blob/public/src/main/java/com/gangweedganggang/cs4240/frontend/TigerFrontend.java)
- [Lowering and instruction selection](https://github.com/rollsafe/cs4240-tiger-compiler/blob/public/src/main/java/com/gangweedganggang/cs4240/backend/InstructionSelection.java) (Architecture independent!!)
- [Briggs register allocator](https://github.com/rollsafe/cs4240-tiger-compiler/blob/public/src/main/java/com/gangweedganggang/cs4240/backend/BriggsRegisterAllocator.java) (Architecture independent!!)
- [Abstract target ISA interface](https://github.com/rollsafe/cs4240-tiger-compiler/blob/public/src/main/java/com/gangweedganggang/cs4240/backend/TargetISA.java)
- [MIPS32 ISA implementation](https://github.com/rollsafe/cs4240-tiger-compiler/blob/public/src/main/java/com/gangweedganggang/cs4240/backend/mips32/MIPS32Target.java)

# How to make:
```bash
make
```

# How to run:
```bash
java -jar cs4240-1.0-SNAPSHOT.jar <file>
```
