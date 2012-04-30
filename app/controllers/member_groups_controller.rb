class MemberGroupsController < ApplicationController
  before_filter :authenticate_user!, :manager?
  before_filter :set_group, :except => [:get_group_join]

  def accept
    join = get_group_join(params)
    join.accept! if join.present?

    redirect_to @group
  end

  def reject
    join = get_group_join(params)
    join.reject! if join.present?

    redirect_to @group
  end

  def remove
    join = get_group_join(params)
    join.destroy if join.present?

    redirect_to @group
  end

  def invite
    @user = User.find(params[:user_id])
    @group_join = @group.group_joins.new(:user => @user, :role => 'member', :accepted => true)
    if @group_join.save!
      redirect_to @group
    else
      redirect_to group_users_path(@group)
    end
  end

  protected

  def set_group
    @group ||= current_user.managing_groups.where(:id => params[:group_id]).first
    redirect_to groups_path if @group.blank?
  end

  def get_group_join(params)
    GroupJoin.where(
      :user_id => params[:id],
      :group_id => params[:group_id],
      :agree => true,
      :role => 'member').first
  end
end