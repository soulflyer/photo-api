#!/bin/bash
#
# This file was copied from ~/.tmux.default
#
# Contains commands to add a new window to an existing tmux session
#
# Note that it is best to create the window in detached state (-d option) so that
# the window is complete before displaying it with select-window. tmux messes up the
# initial display if you don't.

# *************************************************************************************
# Build your window here
# *************************************************************************************
# tmux set default-path $PATHNAME
# tmux new-window -d -n $LABEL
TME=`which tmux`
echo "Running dottmux/default"
echo $LABEL
$TME split-window -d -h -t $LABEL
$TME split-window -d -v -t $LABEL
#set the layout by hand then call tmux list-windows to get the incantation for select-layout
if ! $TME select-layout -t $LABEL "5c65,204x63,0,0{111x63,0,0,92x63,112,0[92x43,112,0,92x19,112,44]}"
then
    echo "Perhaps you are running a different version of tmux? Set the layout by hand and then run tmux list-windows to get a suitable layout string for your machine. Add it in to .tmux.default"
    $TME select-layout -t $LABEL "49a1,227x84,0,0{113x84,0,0,7,113x84,114,0[113x60,114,0,6,113x23,114,61,4]}"
fi
$TME select-window -t $LABEL

$TME send-keys -t :$LABEL.1 'ec src/clj/photo_api' C-m
$TME send-keys -t :$LABEL.2 'ec .' C-m

# Put the cursor in pane 2 (the top right pane, running emacs client)
$TME select-pane -t 2
# *************************************************************************************
# tmux send-keys -t :$LABEL.2 M-x 'cider-jack-in' C-m
# sleep 90
# tmux send-keys -t :$LABEL.2 '(mount/start)'
