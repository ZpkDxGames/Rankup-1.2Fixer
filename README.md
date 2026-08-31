# Rankup 1.2 Fixer

A maintained compatibility fork of **Rankup 1.2**, focused on PlexonCraft server use and quality-of-life fixes while preserving the original rank progression/database behavior.

## Current fix: configurable `/rank` GUI lore

Each rank can optionally define its own GUI lore inside `ranks.yml`:

```yaml
ranks:
  '1':
    display-name: "&8[&9R&6001&8]"
    tag: "&8[&9R&6001&8]"
    requirements:
      money: 1000.0
      xp-levels: 5
      play-time-minutes: 60
    rewards:
      commands:
        - "adjustbonusclaimblocks %player% 1000"
      permissions:
        - "rank.1"
      display:
        - "&8• &a+1,000 &7Claim Blocks"
    menu:
      lore:
        - "&8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        - "&7Status: %status%"
        - ""
        - "&fRequirements"
        - "&8• &7Money: &e$%money%"
        - "&8• &7XP Levels: &e%xp%"
        - "&8• &7Playtime: &e%playtime% min"
        - ""
        - "&fRewards"
        - "%rewards%"
        - "&8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
```

### Supported lore placeholders

- `%status%` — completed / next / locked state from the active language file
- `%rank_id%`
- `%rank_display%`
- `%rank_tag%`
- `%money%` — formatted money requirement
- `%xp%`
- `%playtime%`
- `%commands_count%`
- `%permissions_count%`
- `%rewards%` — special whole-line token that expands `rewards.display`

If `menu.lore` is absent or empty, the original Rankup 1.2 lore is used automatically.

## Compatibility

- Java 21
- Paper API 1.21.x
- Existing Rankup 1.2 SQLite/MySQL player data is unchanged
- Existing `ranks.yml` files remain compatible
- No reward, permission, rank-up, or database migration is required

## Credits

Based on the original Rankup project by **comonier**. This repository contains fixes and maintenance changes for the PlexonCraft deployment.
