
                     Welcome to the *GAdmin*
                ____    _       _           _
               / ___|  / \   __| |_ __ ___ (_)_ __
              | |  _  / _ \ / _` | '_ ` _ \| | '_ \
              | |_| |/ ___ \ (_| | | | | | | | | | |
               \____/_/   \_\__,_|_| |_| |_|_|_| |_|


 Usage:
 
 * Show this screen
          curl -s "{{host}}"

 * Run script on server
          curl -s -X POST "{{host}}" -T <local script file>
          	or
          curl -s -X POST "{{host}}" -d <script text>

 * Install GAN client
          curl -s "{{host}}?r=install" | bash

