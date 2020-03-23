/***
 * @name: findUserByRole
 */
select
 u.*
from
    admin_user u
left join admin_user_role ur on ur.user_id = u.id
left join admin_role r on r.id = ur.role_id
where 
    1=1 
    [#if query.roleIds??]
        and r.id in ( :query.roleIds )
    [/#if]
    [#if query.roleNames??]
        and r.`name` in ( :query.roleNames )
    [/#if]

/***
 * @name: findUserByIds
 */
select
 u.user_Name,
 u.nick_Name,
 u.mobile,
 u.status,
 u.email
from
    admin_user u
where 
    u.id in (:ids)