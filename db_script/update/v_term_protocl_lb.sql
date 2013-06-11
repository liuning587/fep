create or replace view v_term_protocl_lb as
select distinct a.logical_addr,a.protocol_no terminalProtocol,b.gp_sn,b.protocol_no loubaoProtocol,b.gp_addr loubao_addr
from C_TERMINAL a,C_GP b,C_PS c
where b.gp_id = c.gp_id
and b.term_id = a.term_id;
