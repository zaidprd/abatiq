# Game Engine

> Mirror PRD v2 Bab 14.

Lihat: [`PRD_v2_Framework_Dan_Scalability.md`](../PRD_v2_Framework_Dan_Scalability.md)

## Struktur

```
core/engine/
├── session/    GameSession, SessionConfig, ScoringRule
├── round/      Round, RoundFactory, StaticRandomRoundStrategy
├── feedback/   FeedbackPolicy, FeedbackEvent
├── timer/      CountdownTimer, TimerState
├── progress/   ProgressTracker
├── reward/     RewardPolicy, Reward
├── react/      ReactionEffect, ReactionRenderer
└── result/     SessionSummary, ResultBuilder
```

## Prinsip Anti-Over-Engineering

- ✅ `RoundFactory` interface + default impl.
- ✅ `ModuleManifest` interface + `ModuleRegistry`.
- ✅ `CardContent` sealed type.
- ❌ No multi-layered sealed interfaces.
- ❌ No BaseGameViewModel abstract berlapis.
- ❌ No ScoringRule sealed interface (cukup data class).
- ❌ No IOC container.
