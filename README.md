# mc-server-plugins

Minecraft server plugins, one directory each. Every plugin here is
self-contained: its own build, its own documentation, its own tests.

| Plugin | What it does | Platform | Status |
|---|---|---|---|
| [ServerCore](ServerCore/) | Economy, server shop, auction house, land claims, player shops, spawn plots with rent, teleports, statistics and leaderboards — built as one system rather than a pile of unrelated commands | Paper 26.2 · Java 25 | 250 tests passing |

## Building

Each plugin builds independently from its own directory:

```bash
cd ServerCore && ./gradlew build
```

The jar lands in that plugin's `build/libs/`. Built jars are not committed —
see the repository's Releases for downloadable builds.

## ServerCore at a glance

- **Bedrock crossplay is a design constraint, not an afterthought.** Every
  action is reachable by a plain left click on a labelled item, because a
  touch device cannot reliably produce a right-click or a shift-click.
- **Money is a `long` of minor units**, never a `double`, and every balance
  change is a conditional `UPDATE` whose success is the affected row count —
  so a double spend cannot commit even when two threads race.
- **Nothing blocks the main thread.** Database access throws if attempted on
  it; every query computes off-thread and delivers on it.

[`ServerCore/docs/conformance-ledger.html`](ServerCore/docs/conformance-ledger.html)
walks all 33 requirements of the original specification with the evidence
behind each — and names the four that still need a human at a keyboard.
