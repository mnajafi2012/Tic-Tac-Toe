java Main [OPTIONS] ...

OPTIONS:
[train/test] [teacherMode] [uptodown_history] [Learner_vs_User] [Learner_vs_Learner] [Learner_vs_Random]

VALUES:
[train/test]: train/test
[teacherMode]: t/f
[uptodown_history]: t/f
[Learner_vs_User]: 1/0
[Learner_vs_Learner]: 1/0
[Learner_vs_Random]: 1/0

NOTES:
The possible values for [Learner_vs_User] [Learner_vs_Learner] [Learner_vs_Random] are either:
100, 010, 001, (or otherwise where the Learner_vs_User is the default game).


---

Examples:

1. Extract maryamNajafi_GD_hw1.zip file.
2. Open a terminal and locate the "bin" folder.
3. For training or testing select one of the following options.

java Main train t t (TEACHERMODE and UP TO DOWN)
Reads previously designed examples from the complementary examples.txt file. Up to down learning from each game history is being considered.

java Main train f t 1 0 0
Allows the learner to play with the user(you) for 20 game sets. Up to down learning from each game history is being considered.

java Main train f f 0 1 0
Allows the learner to play with another learner with the same weight initialization for 500 game sets. Down to up learning from each game history is being considered.

java Main train f t 0 0 1
Allows the learner to play with a randomly_picking_board_states player for 500 game sets. Up to down learning from each game history is being considered.

java Main test 31.448 23.261 8.708 7.281 -20.324 61.346 0.100
Allows the user to test the model by playing with machine using the above set of trained weights.


---
Best Trained Weights

trained after 30 game sets between learner vs. user:
31.448	23.26   8.708  7.281  -20.324  61.346  0.100

trained after 500 game sets between learner vs. learner:
60.658   9.836   4.113   -4.113   -15.234  0.000  0.100

trained after 500 sets of game between learner vs. random player:
Not significant. The figure in the report illustrates the learning process.

trained using teaching mode after 10 game sets:
8.988	9.252	11.488	9.431	-2.386	44.097	-18.464

---
List of Complementary and Source Files:
Main.java (the driver)
Board.java
PerformanceSystem.java
Critic.java
Generalizer.java
ExperimentGenerator.java
Pair.java (auxiliary file)


