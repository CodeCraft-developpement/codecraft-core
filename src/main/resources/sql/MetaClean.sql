DELETE FROM meta_action WHERE module = 'codecraft-core'
                          AND id NOT IN (SELECT action FROM meta_menu WHERE action IS NOT NULL);

DELETE FROM meta_view WHERE module = 'codecraft-core';