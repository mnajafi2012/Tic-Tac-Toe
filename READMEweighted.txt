java Main [OPTIONS] ...

OPTIONS:
[teacherMode] [weighted_boardstates] [uptodown_history] [Learner_vs_User] [Learner_vs_Learner] [Learner_vs_Random]

VALUES:
[teacherMode]: t/f
[weighted_boardstates]: t/f
[uptodown_history]: t/f
[Learner_vs_User]: 1/0
[Learner_vs_Learner]: 1/0
[Learner_vs_Random]: 1/0

NOTES:
The possible values for [Learner_vs_User] [Learner_vs_Learner] [Learner_vs_Random] are either:
100, 010, 001, (or otherwise where the Learner_vs_User is the default game).

Examples:
java Main f t t
Reads previously designed examples from the adjacency examples.txt file. Up to down learning from each game history is being considered.

java Main t t t 1 0 0
Allows the learner to play with the user(you) for 20 game sets. Up to down learning from each game history is being considered.

java Main t t f 0 1 0
Allows the learner to play with another learner with the same weight initialization for 500 game sets. Down to up learning from each game history is being considered.

java Main t t t 0 0 1
Allows the learner to play with a randomly_picking_board_states player for 500 game sets. Up to down learning from each game history is being considered.

 
