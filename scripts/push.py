#!/usr/bin/python
import argparse
import subprocess
import git
import os


parser = argparse.ArgumentParser(description='Push using git subtrees')
parser.add_argument('--deploy', '-d',  dest='deploy', action='store_true',
                    help='deploy the project to heroku')
parser.add_argument('--prod', '-p',  dest='prod', action='store_true',
                    help='deploy to production')
parser.add_argument('--section', '-s',  dest='section',
                    help='which parts of the project to deploy (default: both)')

args = parser.parse_args()

both = False
if not args.section:
    both = True

curr_dir = os.getcwd()
git_repo = git.Repo(curr_dir, search_parent_directories=True)
git_root = git_repo.git.rev_parse("--show-toplevel")
os.chdir(git_root)


git_status = git_repo.git.diff_index('HEAD')
if git_status != '':
    print('Please clean working tree before attempting a subtree push')
    exit()

head = git_repo.head
branch = head.name

if args.section == 'api' or both:
    subprocess.call('git subtree pull --prefix thavalon-api api-subtree master', shell=True)
    subprocess.call('git subtree push --prefix thavalon-api api-subtree master', shell=True)
    if args.deploy:
        if args.prod:
            subprocess.call('git push api \'refs/remotes/api-subtree/*:refs/heads/*\'', shell=True)
        else:
            subprocess.call('git push api-qa \'refs/remotes/api-subtree/*:refs/heads/*\'', shell=True)

if args.section == 'frontend' or both:
    subprocess.call('git subtree pull --prefix thavalon-frontend frontend-subtree master', shell=True)
    subprocess.call('git subtree push --prefix thavalon-frontend frontend-subtree master', shell=True)

    if args.deploy:
        if args.prod:
            subprocess.call('git push frontend \'refs/remotes/frontend-subtree/*:refs/heads/*\'', shell=True)
        else:
            subprocess.call('git push frontend-qa \'refs/remotes/frontend-subtree/*:refs/heads/*\'', shell=True)

# if args.section == 'api' or both:
