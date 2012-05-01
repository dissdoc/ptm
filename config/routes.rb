Ptm::Application.routes.draw do
  match '/auth/:provider/callback', :to => 'accounts#create'

  devise_for :users, :controllers => { :registrations => 'auths' }
  resources :accounts

  resources :albums do
    get 'my', :on => :collection
    get 'share'
    post 'set_title', :on => :member

    resources :photos do
      member do
        post 'add_note'
        get 'show_notes'
      end
    end
  end

  resources :groups do
    post 'join'
    post 'invite'
    post 'not_agree'
    post 'link_photo'

    get 'users'
    get 'photos'

    resource :member_groups, :only => [:index] do
      member do
        post 'accept'
        post 'reject'
        post 'remove'
        post 'invite'
      end
    end

    resources :dashboards do
      member do
        post 'add_note'
      end
    end
  end

  resources :friendlists
  match '/friendlists/add_friend', :to => 'friendlists#add_friend', :via => :post
  match '/friendlists/remove_friend', :to => 'friendlists#remove_friend', :via => :post

  root :to => 'home#index'
  match 'profile', :to => 'home#profile'
  match 'users', :to => 'home#users'
  match 'myfriends', :to => 'home#myfriends'
  match 'faq', :to => 'home#faq'
  match 'contacts', :to => 'home#contacts'
  match 'about', :to => 'home#about'
  match 'activities', :to => 'home#activities'
end
