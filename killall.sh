ssh dc41.utdallas.edu 'killall -9 java && exit'
ssh dc42.utdallas.edu 'killall -9 java && exit'
ssh dc43.utdallas.edu 'killall -9 java && exit'
ssh dc44.utdallas.edu 'killall -9 java && exit'
ssh dc35.utdallas.edu 'killall -9 java && exit'
kill -9 `ps aux | grep java | grep -v grep | awk '{print $2}'`;
