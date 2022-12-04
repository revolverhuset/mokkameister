-- name: create-brewings-table!
CREATE TABLE brewings (
       id INTEGER PRIMARY KEY AUTOINCREMENT,
       "slack-user" VARCHAR(128) NOT NULL,
       "brew-time" INTEGER NOT NULL,
       "coffee-type" VARCHAR(50) NOT NULL,
       created TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
)

-- name: drop-brewings-table!
DROP TABLE brewings

-- name: insert-brewing<!
INSERT INTO brewings ("slack-user", "brew-time", "coffee-type") VALUES (:slack_user, :brew_time, :coffee_type)

-- name: find-brewing-by-id
SELECT * FROM brewings WHERE id = :id LIMIT 1

-- name: find-latest-brewings
SELECT * FROM brewings ORDER BY created DESC LIMIT 20

-- name: find-last-regular-coffee
SELECT * FROM brewings
WHERE "coffee-type" = 'regular'
ORDER BY created DESC
LIMIT 1

-- name: find-last-instant-coffee
SELECT * FROM brewings
WHERE "coffee-type" = 'instant'
ORDER BY created DESC
LIMIT 1

-- name: month-stats
SELECT count(*) as count, strftime('%Y-%m', created) as monthyear
FROM brewings
WHERE "coffee-type" = 'regular'
GROUP BY monthyear
ORDER BY monthyear;

-- name: coffee-type-stats
SELECT
    (SELECT count(*)
     FROM brewings
     WHERE created >= DATE('now')
     AND "coffee-type" = :coffee_type) as today,
    (SELECT count(*)
     FROM brewings
     WHERE created BETWEEN DATE('now', '-1 day') AND DATE('now')
     AND "coffee-type" = :coffee_type) as yesterday,
    (SELECT count(*)
     FROM brewings
     WHERE DATE(created) > DATE('now', 'weekday 0', '-7 days')
     AND "coffee-type" = :coffee_type) as thisweek,
    (SELECT count(*)
     FROM brewings
     WHERE DATE(created) > DATE('now', 'weekday 0', '-14 days')
     AND DATE(created) <= DATE('now', 'weekday 0', '-7 days')
     AND "coffee-type" = :coffee_type) as lastweek,
    (SELECT count(*)
     FROM brewings
     WHERE created >= DATE('now', 'start of month')
     AND "coffee-type" = :coffee_type) as thismonth,
    (SELECT count(*)
     FROM brewings
     WHERE created < DATE('now', 'start of month')
     AND created >= DATE('now', 'start of month', '-1 month')
     AND "coffee-type" = :coffee_type) as lastmonth,
    (SELECT ROUND(AVG(permonth.count))
     FROM (SELECT count(*) as count, strftime('%Y-%m', created) AS monthyear
     FROM brewings WHERE "coffee-type" = :coffee_type AND
          strftime('%Y-%m', created) != strftime('%Y-%m', DATE('now'))
     GROUP BY monthyear) as permonth) as avgmonth,
    (SELECT count(*)
     FROM brewings
     WHERE "coffee-type" = :coffee_type) as total
