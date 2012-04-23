用有限状态机开发事件驱动程序比一般的过程式编程要复杂一些；一般来说，需要更多的规则，尤其是更多的设计精力。如果处理得当，有限状态机可以使代码简单、测试迅速、维护轻松。但是，即便如此，有限状态机的复杂性使其并不能适合所有事件驱动的程序的开发。例如，当事件的种类不多或事件触发的动作总是相同时，进行额外的开发可能会得不偿失。


Case:
1.The ghosts’ behavior in Pac-Man is implemented as a finite state machine. There is one Evade state, which is the same for all ghosts, and then each ghost has its own Chase state, the actions of which are implemented differently for each ghost. The input of the player eating one of the power pills is the condition for the transition from Chase to Evade. The input of a timer running down is the condition for the transition from Evade to Chase.
2.Quake-style bots are implemented as finite state machines. They have states such as FindArmor, FindHealth, SeekCover, and RunAway. Even the weapons in Quake implement their own mini finite state machines. For example, a rocket may implement states such as Move, TouchObject, and Die.
3.Players in sports simulations such as the soccer game FIFA2002 are implemented as state machines. 
4.The NPCs (non-player characters) in RTSs (real-time strategy games) such as Warcraft make use of finite state machines. They have states such as MoveToPosition, Patrol, and FollowPath.