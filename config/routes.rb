Ptm::Application.routes.draw do
  match '/auth/:provider/callback', :to => 'accounts#create'

  devise_for :users, :controllers => { :registrations => 'auths' }
  resources :accounts

  resources :albums do
    get 'share'
    post 'set_title', :on => :member
    post 'tagging'

    resources :photos do
      member do
        post 'add_note'
        post 'create_recommend'
        post 'apply_recommend'
        post 'destroy_recommend'
        post 'cropped'

        get 'show_notes'
        get 'recommend_geo'
        get 'crop'
        get 'selected'
      end
    end
  end
  match '/albums/link_photo', :to => 'albums#link_photo', :via => :post

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

  resources :messages
  match '/messages/recommended', :to => 'messages#recommended', :via => :post

  resources :familytrees
  match '/familytrees/add_parent', :to => 'familytrees#add_parent', :via => :post

  match '/tags/create', :to => 'tags#create', :via => :post
  match '/tags/destroy', :to => 'tags#destroy', :via => :post

  resources :assortments do
    get 'add_photo_show'
    post 'add_photo'
    post 'remove_photo'
  end

  root :to => 'home#index'
  match 'profile', :to => 'home#profile'
  match 'users', :to => 'home#users'
  match 'faq', :to => 'home#faq'
  match 'contacts', :to => 'home#contacts'
  match 'about', :to => 'home#about'
  match 'favorites', :to => 'home#favorites'
  match '/detail', :to => 'home#detail'

  match '/favorites/fave', :to => 'favorites#fave', :via => :post
  match '/favorites/unfave', :to => 'favorites#unfave', :via => :post
end
