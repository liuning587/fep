select distinct logical_addr from R_COMM_LOG a
where not exists (select 1 from c_terminal b where a.logical_addr = b.logical_addr)
