class Activity < ActiveRecord::Base
  belongs_to :user
  belongs_to :target, :polymorphic => true
  belongs_to :owner, :polymorphic => true

  default_scope :order => 'activities.created_at DESC', :limit => 10

  ADD_FAVE_PHOTO = 'added photo as a favorite'

  def self.add(user, target, action, owner)
    return false if user.blank? or owner.blank? or action.blank?

    activity = Activity.new(:user => user, :owner => owner, :action => action, :target => target)
    activity.save!
  end
end
